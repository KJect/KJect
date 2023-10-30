package me.kject.dependency.trace

import kotlin.reflect.KClass

/**
 * A [DependencyTrace] is the trace of the requests of dependencies.
 *
 * @see DependencyTraceElement
 */
interface DependencyTrace {

    /**
     * The current [DependencyTraceElement]s of this [DependencyTrace].
     */
    val elements: List<DependencyTraceElement>

    /**
     * This [DependencyTrace] as a string.
     */
    override fun toString(): String

    /**
     * This [DependencyTrace] as a string,
     * where each [DependencyTraceElement] that contains the given [duplicate] class is marked.
     */
    fun toString(duplicate: KClass<*>): String

}