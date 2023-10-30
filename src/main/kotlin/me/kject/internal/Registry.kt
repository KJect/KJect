package me.kject.internal

import me.kject.dependency.trace.DependencyTraceBuilder
import me.kject.exception.NotFoundException
import java.util.Collections
import kotlin.reflect.KClass
import kotlin.reflect.cast

internal object Registry {

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
        TODO()
    }

    suspend fun disposeInstances() {
        TODO()
    }

}