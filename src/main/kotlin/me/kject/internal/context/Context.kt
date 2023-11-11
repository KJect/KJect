package me.kject.internal.context

import me.kject.internal.KJectImpl

object Context {

    fun getContextValue(context: String): ContextValue {
        if (context == KJectImpl.context) return ContextValue.CURRENT
        if (context == "*") return ContextValue.ALL

        return ContextValue.NONE
    }

    fun <T> getBestMatch(instances: Map<T, ContextValue>, none: () -> T, multiple: () -> T): T {
        var current: T? = null
        var currentValue = ContextValue.NONE

        for ((instance, value) in instances) {
            if (value == ContextValue.NONE) continue

            if (value > currentValue) {
                current = instance
                currentValue = value
            } else if (value == currentValue) {
                return multiple()
            }
        }

        if (current == null) return none()
        return current
    }

    fun <T> getBestMatch(
        instances: Iterable<T>,
        context: (T) -> String?,
        none: () -> T,
        multiple: () -> T,
    ): T {
        val instanceMap = mutableMapOf<T, ContextValue>()
        for (instance in instances) {
            val value = getContextValue( context(instance) ?: continue)
            if (value == ContextValue.NONE) continue

            instanceMap[instance] = value
        }

        return getBestMatch(instanceMap, none, multiple)
    }

}