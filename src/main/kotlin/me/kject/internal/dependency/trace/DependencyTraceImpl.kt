package me.kject.internal.dependency.trace

import me.kject.dependency.trace.DependencyTrace
import me.kject.dependency.trace.DependencyTraceElement
import kotlin.reflect.KClass

class DependencyTraceImpl(override val elements: List<DependencyTraceElement>) : DependencyTrace {

    override fun toString() = buildString {
        var start = true

        for (i in elements.indices) {
            val element = elements[i]

            if (start) start = false
            else append(" -> ")

            append(element)
            if (i != elements.lastIndex) appendLine()
        }

        if (start) append("No dependencies")
    }

    override fun toString(duplicate: KClass<*>)= buildString {
        var start = true

        for (i in elements.indices) {
            val element = elements[i]

            if (start) start = false
            else append(" -> ")

            append(element)
            if (duplicate in element.classes) append(" <- this is duplicate!")

            if (i != elements.lastIndex) appendLine()
        }

        if (start) append("No dependencies")
    }


}