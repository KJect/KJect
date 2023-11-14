package me.kject.exception.call

import me.kject.exception.KJectException
import kotlin.reflect.KFunction

/**
 * An exception that is set as the completion result of a returned differed when the job
 * of a suspending [function] that was cancelled.
 */
@Suppress("MemberVisibilityCanBePrivate")
class CallCanceledException(val function: KFunction<*>) :
    KJectException("The job of the call to $function was cancelled.")