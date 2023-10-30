package me.kject.internal.dependency.trace

import me.kject.dependency.trace.*

internal class DependencyTraceBuilderImpl : DependencyTraceBuilder {

    private var through: RequestType? = null

    override val elements = mutableListOf<DependencyTraceElement>()

    override fun through(requestType: RequestType) {
        through = requestType
    }

    override fun add(element: DependencyTraceElement) {
        if (element is ClassElement) element.through = through
        elements += element

        through = null
    }

    override fun removeLast() {
        elements.removeLastOrNull()
    }

    override fun build() = DependencyTraceImpl(elements.toList())
}