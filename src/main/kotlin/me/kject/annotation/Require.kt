package me.kject.annotation

import kotlin.reflect.KClass

/**
 * Marks that this class requires some other classes to work.
 *
 * If a [context] is provided, the requirement will only be enforced when KJect is launched in the given [context].
 * If [context] is "*" the requirement will be enforced in all contexts.
 *
 * Multiple [context]s can be defined by using the [Require] annotation multiple times.
 *
 * The target class will be initialized before this class and disposed after this class.
 */
@Repeatable
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Require(val required: KClass<*>, val context: String = "*")
