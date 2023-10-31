package me.kject.exception.call

import me.kject.exception.KJectException
import kotlin.reflect.KFunction

/**
 * An exception that is thrown when the call to a [function] fails with the given [cause].
 */
@Suppress("MemberVisibilityCanBePrivate")
class CallFailedException(val function: KFunction<*>, cause: Throwable) :
    KJectException("The call to $function failed (an exception occurred).", cause)