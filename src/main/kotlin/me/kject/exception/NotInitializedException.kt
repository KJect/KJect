package me.kject.exception

/**
 * An exception that is thrown when the KJect framework is used but not initialized or already disposed.
 */
class NotInitializedException : KJectException("KJect is not initialized or already disposed.")