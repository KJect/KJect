package me.kject.exception.parameter

import me.kject.exception.KJectException

/**
 * An exception that is thrown when a parameter should be set but something went wrong.
 */
sealed class ParameterException(message: String) : KJectException(message)