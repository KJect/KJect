package me.kject.exception

/**
 * An exception that is thrown when disposing some instances fails.
 */
@Suppress("MemberVisibilityCanBePrivate")
class DisposeFailedException(val instances: List<Any>) : KJectException("Failed to dispose ${instances.size}")