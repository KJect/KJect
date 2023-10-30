package me.kject.exception.create

import me.kject.dependency.trace.DependencyTrace
import me.kject.exception.KJectException
import kotlin.reflect.KClass

/**
 * An exception that is thrown when the KJect framework finds the [duplicate] two times in the [dependencyTrace].
 */
@Suppress("MemberVisibilityCanBePrivate")
class CircularDependencyException(val duplicate: KClass<*>, val dependencyTrace: DependencyTrace) :
        KJectException("Circular dependency found: \n${dependencyTrace.toString(duplicate)}.")