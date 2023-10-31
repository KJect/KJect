package me.kject.annotation

/**
 * Informs KJect witch [tactic] to use when calling a suspending function.
 *
 * If a [context] is provided, this tactic will only be used when KJect is launched in the given [context].
 * If [context] is "*" the tactic will be used in all contexts.
 *
 * Multiple [context]s can be defined by using the [With] annotation multiple times.
 *
 * If one [With] annotation with a specific context and one [With] annotation with the "*" context match the current context,
 * the specific context [With] annotation will be used.
 *
 * If two [With] annotations with specific contexts match the current context, an exception will be thrown.
 */
@Repeatable
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class With(val tactic: Tactic = Tactic.JOIN, val context: String = "*") {

    /**
     * The tactic to use when calling a suspending function.
     */
    enum class Tactic {

        /**
         * Launches a new coroutine without any specific dispatcher.
         */
        LAUNCH,

        /**
         * Use [LAUNCH] and join the coroutine,
         * to effectively suspend the parent coroutine until the child coroutine is finished.
         */
        JOIN,

        /**
         * Launches a new coroutine on the [Default][kotlinx.coroutines.Dispatchers.Default] dispatcher.
         */
        DEFAULT,

        /**
         * Launches a new coroutine on the [Unconfined][kotlinx.coroutines.Dispatchers.Unconfined] dispatcher.
         *
         * This will start the coroutine on the current thread, until the first suspension point.
         * After that, the coroutine will be resumed on a new thread.
         */
        UNCONFINED,

        /**
         * Launches a new coroutine on the [IO][kotlinx.coroutines.Dispatchers.IO] dispatcher.
         */
        IO,

    }

}
