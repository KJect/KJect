package me.kject.annotation

import kotlin.reflect.KClass

/**
 * Marks a class as being a facade for a given [building].
 *
 * If a [context] is provided, this facade will only be used when KJect is launched in the given [context].
 * If [context] is "*" the facade will be used in all contexts.
 *
 * Multiple [context]s can be defined by using the [Facade] annotation multiple times.
 *
 * If a "*" context and a specific context facade match the current context, the specific context facade will be used.
 *
 * If two specific context facades match the current context, an exception will be thrown.
 *
 * If any KJect function is called with the facade as a parameter, the [building] will be used instead
 * (e.g. getting the facade from the system will return the result of getting the [building] from the system).
 */
@Repeatable
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Facade(val building: KClass<*>, val context: String = "*")
