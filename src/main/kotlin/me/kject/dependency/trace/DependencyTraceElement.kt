package me.kject.dependency.trace

import kotlin.reflect.KClass

/**
 * A single element in a dependency trace.
 */
sealed interface DependencyTraceElement {

    /**
     * The classes that are requested by this element.
     */
    val classes: List<KClass<*>>

}