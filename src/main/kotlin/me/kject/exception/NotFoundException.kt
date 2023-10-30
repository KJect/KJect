package me.kject.exception

import kotlin.reflect.KClass

/**
 * An exception that is thrown when an instance of the given [type] is tries to be retrieved but no instance of the
 * given [type] was found.
 */
@Suppress("MemberVisibilityCanBePrivate")
class NotFoundException(val type: KClass<*>): KJectException("No instance of $type found.")