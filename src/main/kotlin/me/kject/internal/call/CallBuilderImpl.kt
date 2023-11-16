package me.kject.internal.call

import me.kject.call.CallBuilder
import me.kject.exception.parameter.NoInstanceParameterException
import me.kject.exception.parameter.NoReceiverParameterException
import me.kject.exception.parameter.UnknownParameterException
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.instanceParameter

class CallBuilderImpl<T>(override val function: KFunction<T>) : CallBuilder<T> {

    private val declaredParameters = mutableMapOf<KParameter, Any?>()

    override val parameters: List<KParameter>
        get() = function.parameters

    override var instance: Any?
        get() = this[function.instanceParameter ?: throw NoInstanceParameterException(function)]
        set(value) {
            this[function.instanceParameter ?: throw NoInstanceParameterException(function)] = value
        }
    override var receiver: Any?
        get() = this[function.extensionReceiverParameter ?: throw NoReceiverParameterException(function)]
        set(value) {
            this[function.extensionReceiverParameter ?: throw NoReceiverParameterException(function)] = value
        }

    override fun set(parameter: KParameter, value: Any?) {
        declaredParameters[parameter] = value
    }

    override fun set(name: String, value: Any?) {
        declaredParameters[function.findParameterByName(name) ?: throw UnknownParameterException(function, name)] = value
    }

    override fun get(parameter: KParameter) = declaredParameters[parameter]

    override fun get(name: String) =
        declaredParameters[function.findParameterByName(name) ?: throw UnknownParameterException(function, name)]

    fun getParameters(): Map<KParameter, Any?> = declaredParameters

}