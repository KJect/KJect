package me.kject.internal

import me.kject.annotation.*
import me.kject.dependency.trace.DependencyTraceBuilder
import me.kject.dependency.trace.RequestType
import me.kject.exception.DisposeFailedException
import me.kject.exception.call.CallFailedException
import me.kject.exception.create.*
import me.kject.internal.call.Caller
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
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

        var usedFacade: Facade? = null
        var directFacade = false

        for (facade in type.findAnnotations<Facade>()) {
            val value = KJectImpl.getContextValue(facade.context)
            if (value == 0) continue
            if (value == 2) {
                if (directFacade) throw MultipleFacadesException(type)

                usedFacade = facade
                directFacade = true
            }

            if (value == 1 && !directFacade) {
                if (usedFacade != null) throw MultipleFacadesException(type)
                usedFacade = facade
            }
        }

        usedFacade?.let {
            if (!it.building.isSubclassOf(type)) throw IllegalFacadeException(it.building, type)
            return type.cast(create(it.building, traceBuilder))
        }

        traceBuilder.through(RequestType.REQUIRE)
        for (require in type.findAnnotations<Require>()) {
            if (KJectImpl.getContextValue(require.context) == 0) continue
            if (require.required !in Registry) create(require.required, traceBuilder)
        }

        type.objectInstance?.let {
            Registry += it

            traceBuilder.through(RequestType.INITIALIZE)
            for (function in type.functions) {
                val annotation = function.findAnnotation<Initialize>() ?: continue
                if (KJectImpl.getContextValue(annotation.context) == 0) continue

                @Suppress("DeferredResultUnused")
                Caller.call(function, {}, traceBuilder)
            }

            -traceBuilder
            return it
        }

        var useConstructor: KFunction<T>? = null
        var directConstructor = false

        for (constructor in type.constructors) {
            val annotation = constructor.findAnnotation<UseConstructor>() ?: continue
            val value = KJectImpl.getContextValue(annotation.context)

            if (value == 0) continue
            if (value == 2) {
                if (directConstructor) throw IllegalConstructorsException(type)

                useConstructor = constructor
                directConstructor = true
            }

            if (value == 1 && !directConstructor) {
                if (useConstructor != null) throw IllegalConstructorsException(type)
                useConstructor = constructor
            }
        }

        if (useConstructor == null) useConstructor = type.primaryConstructor ?: throw IllegalConstructorsException(type)

        traceBuilder.through(RequestType.CONSTRUCTOR)
        val instance = Caller.call(useConstructor, {}, traceBuilder).await()

        traceBuilder.through(RequestType.INITIALIZE)
        for (function in type.functions) {
            val annotation = function.findAnnotation<Initialize>() ?: continue
            if (KJectImpl.getContextValue(annotation.context) == 0) continue

            @Suppress("DeferredResultUnused")
            Caller.call(function, {
                this.instance = instance
            }, traceBuilder)
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

                done += instance

                function@ for (function in instance::class.functions) {
                    val annotation = function.findAnnotation<Dispose>() ?: continue@function
                    if (KJectImpl.getContextValue(annotation.context) == 0) continue@function

                    @Suppress("DeferredResultUnused")
                    try {
                        Caller.call(function, {
                            this.instance = instance
                        }, DependencyTraceBuilder.create(), onDispose = true)
                    } catch (e: CallFailedException) {
                        throw DisposeFailedException(instances)
                    }
                }
            }

            instances.removeAll(done)

            if (before == instances.size) throw DisposeFailedException(instances)
        }
    }

}