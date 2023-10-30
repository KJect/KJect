package me.kject.exception.create

import me.kject.exception.KJectException
import kotlin.reflect.KClass

/**
 * An exception that is thrown when the KJect framework finds multiple eligible constructors on the given [type].
 */
@Suppress("MemberVisibilityCanBePrivate")
class MultipleConstructorsException(val type: KClass<*>) :
    KJectException("Multiple eligible constructors found on $type.")