package me.kject.exception

/**
 * An exception that is thrown when something goes wrong in the KJect framework.
 */
open class KJectException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)