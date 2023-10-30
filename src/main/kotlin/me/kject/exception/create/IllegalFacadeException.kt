package me.kject.exception.create

import me.kject.exception.KJectException
import kotlin.reflect.KClass

/**
 * An exception that is thrown when the KJect framework finds a [building], that doesn't implement its [facade].
 */
@Suppress("MemberVisibilityCanBePrivate")
class IllegalFacadeException(val building: KClass<*>, val facade: KClass<*>) :
    KJectException("The building $building doesn't implement its facade $facade.")