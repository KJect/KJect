package me.kject.dependency.trace

import kotlin.reflect.KClass

/**
 * Represents a class that was requested in the dependency trace.
 */
class ClassElement(val klass: KClass<*>) : DependencyTraceElement {

    /**
     * The type, why this class was requested.
     */
    var through: RequestType? = null
        internal set

    override fun toString() = "$klass ${through?.let { "(through $it)" } ?: ""}"

}