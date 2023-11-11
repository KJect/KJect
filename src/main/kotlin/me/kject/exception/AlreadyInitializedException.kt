package me.kject.exception

/**
 * An exception that is thrown when the KJect framework is tried to be initialized twice without disposing it.
 */
class AlreadyInitializedException : KJectException("KJect is already initialized.")