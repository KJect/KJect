package me.kject.internal.call

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import me.kject.annotation.Inject
import me.kject.annotation.With
import me.kject.call.CallBuilder
import me.kject.dependency.trace.DependencyTraceBuilder
import me.kject.exception.call.BadParameterException
import me.kject.exception.call.CallCanceledException
import me.kject.exception.call.CallFailedException
import me.kject.internal.Coroutine
import me.kject.internal.KJectImpl
import me.kject.internal.Registry
import java.lang.reflect.InvocationTargetException
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.hasAnnotation

object Caller {

    suspend fun <T> call(
        function: KFunction<T>,
        builder: CallBuilder<T>.() -> Unit,
        dependencyTraceBuilder: DependencyTraceBuilder,
        onDispose: Boolean = false
    ): Deferred<T> {
        val arguments = CallBuilderImpl(function).apply(builder).getParameters()

        val parameterMap = mutableMapOf<KParameter, Any?>()
        for (parameter in function.parameters) {
            if (parameter.hasAnnotation<Inject>()) (parameter.type.classifier as? KClass<*>)
                ?.let { parameterMap[parameter] = Registry[it] ?: Registry.create(it, dependencyTraceBuilder) }
            else if (parameter in arguments) parameterMap[parameter] = arguments[parameter]
            else if (!parameter.isOptional && !parameter.type.isMarkedNullable)
                throw BadParameterException(parameter, function)
        }

        var tactic: With.Tactic? = null
        var directTactic = false
        for (with in function.findAnnotations<With>()) {
            val value = KJectImpl.getContextValue(with.context)

            if (value == 2) {
                if (directTactic) TODO("Throw correct exception")

                tactic = with.tactic
                directTactic = true
            }

            if (value == 1 && !directTactic) {
                if (tactic != null) TODO("Throw correct exception")
                tactic = with.tactic
            }
        }

        if (tactic == null) tactic = With.Tactic.JOIN
        val deferred = Coroutine.async(
            when (tactic) {
                With.Tactic.JOIN, With.Tactic.LAUNCH -> EmptyCoroutineContext
                With.Tactic.DEFAULT -> Dispatchers.Default
                With.Tactic.UNCONFINED -> Dispatchers.Unconfined
                With.Tactic.IO -> Dispatchers.IO
            },
            onDispose,
        ) {
            try {
                function.callSuspendBy(parameterMap)
            } catch (e: InvocationTargetException) {
                throw CallFailedException(function, e.targetException)
            }
        }

        if (tactic == With.Tactic.JOIN) {
            try {
                deferred.await()
            } catch (e: CancellationException) {
                throw CallCanceledException(function)
            }
        }

        return deferred
    }

}