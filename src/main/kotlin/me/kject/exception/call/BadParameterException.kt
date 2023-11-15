package me.kject.exception.call

import me.kject.exception.KJectException
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

/**
 * An exception that is thrown when the given [parameter] of the given [function] can't be set.
 */
class BadParameterException(val parameter: KParameter, val function: KFunction<*>) :
    KJectException("Could not set $parameter off $function")