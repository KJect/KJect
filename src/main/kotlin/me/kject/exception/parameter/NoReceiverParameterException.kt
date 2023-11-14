package me.kject.exception.parameter

import kotlin.reflect.KFunction

/**
 * An exception that is thrown when the receiver parameter should be set but
 * the [function] doesn't have a receiver parameter.
 */
class NoReceiverParameterException(val function: KFunction<*>) :
    ParameterException("No receiver parameter in $function")