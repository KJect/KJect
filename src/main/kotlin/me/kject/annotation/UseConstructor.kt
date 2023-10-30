package me.kject.annotation

/**
 * Markes this constructor to be preferred over other constructors when KJect is creating an instance of the class.
 *
 * If a [context] is provided, the preference will only be enforced when KJect is launched in the given [context].
 * If [context] is "*" the preference will be enforced in all contexts.
 *
 * Multiple [context]s can be defined by using the [UseConstructor] annotation multiple times.
 *
 * If one constructor matches the current context and another constructor matches the "*" context,
 * the specific context constructor will be used.
 *
 * If two constructors match the current context, an exception will be thrown.
 */
@Repeatable
@MustBeDocumented
@Target(AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.RUNTIME)
annotation class UseConstructor(val context: String = "*")