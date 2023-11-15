package me.kject.exception.call

import me.kject.exception.KJectException
import kotlin.reflect.KFunction

/**
 * An exception that is thrown when the KJect framework finds multiple withs on the given [function].
 */
class MultipleOnException(val function: KFunction<*>) :
    KJectException("$function has multiple @On annotations")