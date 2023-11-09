package me.kject.exception.create

import me.kject.exception.KJectException
import kotlin.reflect.KClass

/**
 * An exception that is thrown when the KJect framework finds multiple faces on the given [type].
 */
@Suppress("MemberVisibilityCanBePrivate")
class MultipleFacadesException(val type: KClass<*>) :
    KJectException("$type has multiple facades")