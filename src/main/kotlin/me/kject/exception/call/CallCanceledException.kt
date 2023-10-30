package me.kject.exception.call

import me.kject.exception.KJectException
import kotlin.reflect.KFunction

/**
 * An exception that is thrown when the job of a suspending [function] that was called with the
 * [Join][me.kject.annotation.With.Tactic.JOIN] tactic is cancelled.
 */
@Suppress("MemberVisibilityCanBePrivate")
class CallCanceledException(val function: KFunction<*>) :
    KJectException("The job of the call to $function was cancelled.")