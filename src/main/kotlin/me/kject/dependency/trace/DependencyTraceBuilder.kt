package me.kject.dependency.trace

import me.kject.internal.dependency.trace.DependencyTraceBuilderImpl
import kotlin.reflect.KClass

/**
 * A [DependencyTraceBuilder] is a builder for a [DependencyTrace].
 *
 * @see DependencyTrace
 */
interface DependencyTraceBuilder {

    /**
     * The current [DependencyTraceElement]s of this [DependencyTraceBuilder].
     */
    val elements: List<DependencyTraceElement>

    /**
     * The classes currently in the [DependencyTrace].
     */
    val classes: List<KClass<*>>

    /**
     * Sets through witch [RequestType] the next [DependencyTraceElement] is requested.
     */
    infix fun through(requestType: RequestType)

    /**
     * Adds a [DependencyTraceElement] to this [DependencyTraceBuilder].
     */
    operator fun plusAssign(element: DependencyTraceElement)

    /**
     * Adds a [ClassElement] with the given [klass] to this [DependencyTraceBuilder].
     */
    operator fun plusAssign(klass: KClass<*>)

    /**
     * Removes the last [DependencyTraceElement] from this [DependencyTraceBuilder].
     */
    operator fun unaryMinus()

    /**
     * Builds a [DependencyTrace] from this [DependencyTraceBuilder].
     */
    fun build(): DependencyTrace

    companion object {

        /**
         * Creates a new [DependencyTraceBuilder].
         */
        fun create(): DependencyTraceBuilder = DependencyTraceBuilderImpl()

    }

}