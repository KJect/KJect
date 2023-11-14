package me.kject.annotation


/**
 * Informs KJect witch [dispatcher] to use when calling a suspending function.
 *
 * If a [context] is provided, this tactic will only be used when KJect is launched in the given [context].
 * If [context] is "*" the tactic will be used in all contexts.
 *
 * Multiple [context]s can be defined by using the [On] annotation multiple times.
 *
 * If one [On] annotation with a specific context and one [On] annotation with the "*" context match the current context,
 * the specific context [On] annotation will be used.
 *
 * If two [On] annotations with specific contexts match the current context, an exception will be thrown.
 */
@Repeatable
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class On(val dispatcher: Dispatcher, val context: String = "*") {

    /**
     * The tactic to use when calling a suspending function.
     */
    enum class Dispatcher {

        /**
         * Uses a coroutine without any specific dispatcher ([EmptyCoroutineContext][kotlin.coroutines.EmptyCoroutineContext]).
         */
        EMPTY,

        /**
         * Uses the [Default][kotlinx.coroutines.Dispatchers.Default] dispatcher.
         */
        DEFAULT,

        /**
         * Uses the [Unconfined][kotlinx.coroutines.Dispatchers.Unconfined] dispatcher.
         *
         * This will start the coroutine on the current thread, until the first suspension point.
         * After that, the coroutine will be resumed on a new thread.
         */
        UNCONFINED,

        /**
         * Uses the [IO][kotlinx.coroutines.Dispatchers.IO] dispatcher.
         */
        IO,

    }

}
