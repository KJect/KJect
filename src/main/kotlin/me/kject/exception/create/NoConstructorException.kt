package me.kject.exception.create

import me.kject.exception.KJectException
import kotlin.reflect.KClass

/**
 * An exception that is thrown when the KJect framework can't find an eligible constructor on the given [type].
 */
@Suppress("MemberVisibilityCanBePrivate")
class NoConstructorException(val type: KClass<*>): KJectException("No eligible constructor found on $type.")