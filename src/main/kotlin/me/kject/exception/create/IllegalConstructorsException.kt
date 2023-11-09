package me.kject.exception.create

import me.kject.exception.KJectException
import kotlin.reflect.KClass

/**
 * An exception that is thrown when the KJect framework finds multiple eligible constructors or
 * no eligible constructor on the given [type].
 */
@Suppress("MemberVisibilityCanBePrivate")
class IllegalConstructorsException(val type: KClass<*>) :
    KJectException("$type has multiple or no eligible constructor(s)")