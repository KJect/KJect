package me.kject.dependency.trace

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * Represents the call of a function in the dependency trace.
 */
@Suppress("MemberVisibilityCanBePrivate")
class FunctionElement(val function: KFunction<*>) : DependencyTraceElement {

    override val classes = emptyList<KClass<*>>()

    override fun toString() = function.toString()

}