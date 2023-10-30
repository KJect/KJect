package me.kject.internal.call

import me.kject.call.CallBuilder
import me.kject.exception.parameter.NoInstanceParameterException
import me.kject.exception.parameter.UnknownParameterException
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.instanceParameter

class CallBuilderImpl<T>(override val function: KFunction<T>) : CallBuilder<T> {

    private val parameters = mutableMapOf<KParameter, Any?>()
    override var instance: Any?
        get() = this[function.instanceParameter ?: throw NoInstanceParameterException(function)]
        set(value) {
            this[function.instanceParameter ?: throw NoInstanceParameterException(function)] = value
        }
    override var receiver: Any?
        get() = this[function.instanceParameter ?: throw NoInstanceParameterException(function)]
        set(value) {
            this[function.instanceParameter ?: throw NoInstanceParameterException(function)] = value
        }

    override fun set(parameter: KParameter, value: Any?) {
        parameters[parameter] = value
    }

    override fun set(name: String, value: Any?) {
        parameters[function.findParameterByName(name) ?: throw UnknownParameterException(function, name)] = value
    }

    override fun get(parameter: KParameter) = parameters[parameter]

    override fun get(name: String) =
        parameters[function.findParameterByName(name) ?: throw UnknownParameterException(function, name)]

    fun getParameters(): Map<KParameter, Any?> = parameters

}