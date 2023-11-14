package me.kject.internal

import me.kject.annotation.*
import me.kject.dependency.trace.DependencyTraceBuilder
import me.kject.dependency.trace.RequestType
import me.kject.exception.DisposeFailedException
import me.kject.exception.call.CallFailedException
import me.kject.exception.create.CircularDependencyException
import me.kject.exception.create.IllegalConstructorsException
import me.kject.exception.create.IllegalFacadeException
import me.kject.exception.create.MultipleFacadesException
import me.kject.internal.call.Caller
import me.kject.internal.context.Context
import me.kject.internal.context.ContextValue
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.*

internal object Registry {

    var allowCreate = false

    private val instances = Collections.synchronizedList(mutableListOf<Any>())

    operator fun contains(type: KClass<*>) = instances.any { type.isInstance(it) }

    operator fun plusAssign(instance: Any) {
        instances += instance
    }

    operator fun <T : Any> get(type: KClass<T>): T? =
        instances
            .firstOrNull { type.isInstance(it) }
            ?.let { type.cast(it) }

    suspend fun <T : Any> create(type: KClass<T>, traceBuilder: DependencyTraceBuilder): T {
        if (type in traceBuilder.classes) {
            traceBuilder += type
            throw CircularDependencyException(type, traceBuilder.build())
        }

        traceBuilder += type

        val facade = Context.getBestMatch(
            type.findAnnotations<Facade>(),
            { it?.context },
            { null },
            { throw MultipleFacadesException(type) },
        )

        facade?.let {
            if (!it.building.isSubclassOf(type)) throw IllegalFacadeException(it.building, type)
            return type.cast(create(it.building, traceBuilder))
        }

        traceBuilder.through(RequestType.REQUIRE)
        for (require in type.findAnnotations<Require>()) {
            if (Context.getContextValue(require.context) == ContextValue.NONE) continue
            if (require.required !in Registry) create(require.required, traceBuilder)
        }

        type.objectInstance?.let {
            traceBuilder.through(RequestType.INITIALIZE)
            for (function in type.functions) {
                val annotation = function.findAnnotation<Initialize>() ?: continue
                if (Context.getContextValue(annotation.context) == ContextValue.NONE) continue

                Caller.call(function, {
                    this.instance = it
                }, traceBuilder).await()
            }

            Registry += it
            -traceBuilder
            return it
        }

        val constructor = Context.getBestMatch(
            type.constructors,
            { it.findAnnotation<UseConstructor>()?.context },
            { type.primaryConstructor ?: throw IllegalConstructorsException(type) },
            { throw IllegalConstructorsException(type) },
        )

        traceBuilder.through(RequestType.CONSTRUCTOR)
        val instance = Caller.call(constructor, {}, traceBuilder).await()

        traceBuilder.through(RequestType.INITIALIZE)
        for (function in type.functions) {
            val annotation = function.findAnnotation<Initialize>() ?: continue
            if (Context.getContextValue(annotation.context) == ContextValue.NONE) continue

            Caller.call(function, {
                this.instance = instance
            }, traceBuilder).await()
        }
        Registry += instance

        -traceBuilder
        return instance
    }

    suspend fun disposeInstances() {
        var before: Int
        val done = mutableSetOf<Any>()
        while (instances.isNotEmpty()) {
            before = instances.size

            instance@ for (instance in instances) {
                for (other in instances) {
                    for (require in other::class.findAnnotations<Require>()) {
                        if (get(require.required) == instance) continue@instance
                    }
                }

                function@ for (function in instance::class.functions) {
                    val annotation = function.findAnnotation<Dispose>() ?: continue@function
                    if (Context.getContextValue(annotation.context) == ContextValue.NONE) continue@function

                    @Suppress("DeferredResultUnused")
                    try {
                        Caller.call(function, {
                            this.instance = instance
                        }, DependencyTraceBuilder.create()).await()
                    } catch (e: CallFailedException) {
                        throw DisposeFailedException(instances.toList())
                    }
                }

                done += instance
            }

            instances.removeAll(done)

            if (before == instances.size) throw DisposeFailedException(instances.toList())
        }
    }

    fun clear() {
        instances.clear()
    }

}