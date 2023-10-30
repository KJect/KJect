package me.kject.call

import me.kject.exception.parameter.NoInstanceParameterException
import me.kject.exception.parameter.NoReceiverParameterException
import me.kject.exception.parameter.UnknownParameterException
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

/**
 * A builder to build the arguments for a call.
 */
interface CallBuilder<T> {

    /**
     * The [function] that is called.
     */
    val function: KFunction<T>

    /**
     * The current value of the instance parameter.
     *
     * @throws NoInstanceParameterException If the function doesn't have an instance parameter.
     */
    var instance: Any?
        @Throws(NoInstanceParameterException::class)
        set
        @Throws(NoInstanceParameterException::class)
        get

    /**
     * The current value of the receiver parameter.
     *
     * @throws NoReceiverParameterException If the function doesn't have a receiver parameter.
     */
    var receiver: Any?
        @Throws(NoReceiverParameterException::class)
        set
        @Throws(NoReceiverParameterException::class)
        get

    /**
     * Sets the [value] for the given [parameter].
     */
    operator fun set(parameter: KParameter, value: Any?)

    /**
     * Sets the [value] for a parameter by its [name].
     *
     * @throws UnknownParameterException If the parameter does not exist.
     */
    @Throws(UnknownParameterException::class)
    operator fun set(name: String, value: Any?)

    /**
     * Gets the [value] for the given [parameter].
     */
    operator fun get(parameter: KParameter): Any?

    /**
     * Gets the [value] for a parameter by its [name].
     *
     * @throws UnknownParameterException If the parameter does not exist.
     */
    @Throws(UnknownParameterException::class)
    operator fun get(name: String): Any?

}