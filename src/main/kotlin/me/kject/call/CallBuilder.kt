package me.kject.call

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
     * Sets the [value] for the given [parameter].
     */
    operator fun set(parameter: KParameter, value: Any?): CallBuilder<T>

    /**
     * Sets the [value] for the given [parameter] by the given [name] of the parameter.
     */
    operator fun set(name: String, value: Any?): CallBuilder<T>

}