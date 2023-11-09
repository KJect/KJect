package me.kject.exception.call

import me.kject.exception.KJectException
import kotlin.reflect.KFunction

/**
 * An exception that is thrown when the KJect framework finds multiple withs on the given [function].
 */
@Suppress("MemberVisibilityCanBePrivate")
class MultipleWithsException(val function: KFunction<*>) :
    KJectException("$function has multiple @With annotations")