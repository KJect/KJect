package me.kject.internal.call

import kotlinx.coroutines.*
import me.kject.annotation.Inject
import me.kject.annotation.With
import me.kject.call.CallBuilder
import me.kject.dependency.trace.DependencyTraceBuilder
import me.kject.exception.call.BadParameterException
import me.kject.exception.call.CallCanceledException
import me.kject.exception.call.CallFailedException
import me.kject.exception.call.MultipleWithsException
import me.kject.internal.KJectImpl
import me.kject.internal.Registry
import me.kject.internal.context.Context
import java.lang.reflect.InvocationTargetException
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.isAccessible

object Caller {

    suspend fun <T> call(
        function: KFunction<T>,
        builder: CallBuilder<T>.() -> Unit,
        dependencyTraceBuilder: DependencyTraceBuilder,
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

        val tactic = Context.getBestMatch(
            function.findAnnotations<With>(),
            { it?.context },
            { null },
            { throw MultipleWithsException(function) },
        )?.tactic ?: With.Tactic.JOIN

        function.isAccessible = true

        val context = when (tactic) {
            With.Tactic.JOIN, With.Tactic.LAUNCH -> EmptyCoroutineContext
            With.Tactic.DEFAULT -> Dispatchers.Default
            With.Tactic.UNCONFINED -> Dispatchers.Unconfined
            With.Tactic.IO -> Dispatchers.IO
        }

        val deferred = CompletableDeferred<T>()
        val job = KJectImpl.scope.launch(context) {
            try {
                val result = function.callSuspendBy(parameterMap)
                deferred.complete(result)
            } catch (e: InvocationTargetException) {
                deferred.completeExceptionally(CallFailedException(function, e.targetException))
            }
        }

        job.invokeOnCompletion {
            if (it is CancellationException) deferred.completeExceptionally(CallCanceledException(function))
        }

        if (tactic == With.Tactic.JOIN) deferred.await()

        return deferred
    }

}