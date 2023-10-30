package me.kject.dependency.trace

import me.kject.internal.dependency.trace.DependencyTraceBuilderImpl

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
     * Sets through witch [RequestType] the next [DependencyTraceElement] is requested.
     */
    fun through(requestType: RequestType)

    /**
     * Adds a [DependencyTraceElement] to this [DependencyTraceBuilder].
     */
    fun add(element: DependencyTraceElement)

    /**
     * Removes the last [DependencyTraceElement] from this [DependencyTraceBuilder].
     */
    fun removeLast()

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