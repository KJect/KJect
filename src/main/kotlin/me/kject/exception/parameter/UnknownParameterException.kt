package me.kject.exception.parameter

import kotlin.reflect.KFunction

/**
 * An exception that is thrown when a parameter should be set but
 * the [function] doesn't have a parameter with the given [name].
 */
@Suppress("MemberVisibilityCanBePrivate")
class UnknownParameterException(val function: KFunction<*>, val name: String) :
    ParameterException("Unknown parameter $name in $function")