package me.kject.dependency.trace

import kotlin.reflect.KFunction

/**
 * Represents the call of a function in the dependency trace.
 */
class FunctionElement(val function: KFunction<*>) : DependencyTraceElement {

    override fun toString() = function.toString()

}