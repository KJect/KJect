package me.kject.exception.create

import me.kject.exception.KJectException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * An exception that is thrown when the KJect framework finds multiple eligible [constructors] on the given [type].
 */
@Suppress("MemberVisibilityCanBePrivate")
class MultipleConstructorsException(val type: KClass<*>, val constructors: List<KFunction<*>>) :
    KJectException("Multiple eligible constructors found on $type: ${constructors.joinToString()}.")