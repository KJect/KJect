package me.kject.exception.parameter

import kotlin.reflect.KFunction

/**
 * An exception that is thrown when the instance parameter should be set but
 * the [function] doesn't have an instance parameter.
 */
class NoInstanceParameterException(val function: KFunction<*>) :
    ParameterException("No instance parameter in $function")