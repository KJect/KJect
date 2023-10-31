package me.kject.exception.create

import me.kject.exception.KJectException

/**
 * An exception that is thrown when a class is tried to be created while KJect is being disposed.
 */
class InDisposeException : KJectException("Cannot create instances while disposing")