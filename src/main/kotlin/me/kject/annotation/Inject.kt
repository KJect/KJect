package me.kject.annotation

/**
 * Markes a parameter on a function or constructor to be injected by KJect.
 *
 * @see me.kject.KJect.call
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Inject
