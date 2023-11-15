package me.kject.internal.dependency.trace

import me.kject.dependency.trace.*
import kotlin.reflect.KClass

internal class DependencyTraceBuilderImpl : DependencyTraceBuilder {

    private var through: RequestType? = null

    override val elements = mutableListOf<DependencyTraceElement>()

    override val classes
        get() = elements.filterIsInstance<ClassElement>().map { it.klass }

    override infix fun through(requestType: RequestType) {
        through = requestType
    }

    override operator fun plusAssign(element: DependencyTraceElement) {
        if (element is ClassElement) element.through = through
        elements += element
    }

    override fun plusAssign(klass: KClass<*>) {
        this += ClassElement(klass)
    }

    override fun unaryMinus() {
        elements.removeLastOrNull()
    }

    override fun build() = DependencyTraceImpl(elements.toList())
}