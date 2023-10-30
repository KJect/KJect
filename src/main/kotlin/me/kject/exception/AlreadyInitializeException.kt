package me.kject.exception

/**
 * An exception that is thrown when the KJect framework is tried to be initialized twice without disposing it.
 */
class AlreadyInitializeException : KJectException(message = "KJect is already initialized.")