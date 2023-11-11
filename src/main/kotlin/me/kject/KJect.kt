package me.kject

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import me.kject.call.CallBuilder
import me.kject.exception.AlreadyInitializeException
import me.kject.exception.DisposeFailedException
import me.kject.exception.NotFoundException
import me.kject.exception.NotInitializeException
import me.kject.exception.call.BadParameterException
import me.kject.exception.call.CallCanceledException
import me.kject.exception.call.CallFailedException
import me.kject.exception.call.MultipleWithsException
import me.kject.exception.create.*
import me.kject.internal.KJectImpl
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * The [KJect] object is the main interaction point with KJect.
 */
object KJect {

    /**
     * Initializes KJect in the given [context].
     *
     * Different context can be used to define witch annotations should be used.
     *
     * The [scope] is used to start jobs for calling functions with different [Tactic][me.kject.annotation.With.Tactic]s.
     * The [scope] **must not be canceled**, until KJect is disposed.
     *
     * @throws AlreadyInitializeException If KJect is already initialized.
     */
    @Throws(AlreadyInitializeException::class)
    suspend fun launch(scope: CoroutineScope, context: String = "production"): Unit = KJectImpl.launch(scope, context)

    /**
     * Disposes KJect.
     *
     * @throws NotInitializeException If KJect is not initialized.
     * @throws DisposeFailedException If KJect fails to dispose some instances.
     */
    @Throws(NotInitializeException::class, DisposeFailedException::class)
    suspend fun dispose(): Unit = KJectImpl.dispose()

    /**
     * Gets an instance of the given [type].
     *
     * Any instance ever created that matches the given [type] will be returned.
     * For this the returned instance does not have to be the same as the given [type],
     * but can also be a subclass of the given [type].
     *
     * @throws NotInitializeException If KJect is not initialized.
     * @throws NotFoundException If no instance of the given [type] is in the registry.
     */
    @Throws(NotInitializeException::class, NotFoundException::class)
    operator fun <T : Any> get(type: KClass<T>): T = KJectImpl[type]

    /**
     * @throws NotInitializeException If KJect is not initialized.
     * @throws NotFoundException If no instance of the given [type][T] is in the registry.
     *
     * @see get
     */
    @Throws(NotInitializeException::class, NotFoundException::class)
    inline fun <reified T : Any> get(): T = get(T::class)

    /**
     * Gets an instance of the given [type] or `null` if no instance of the given [type] is in the registry.
     *
     * @throws NotInitializeException If KJect is not initialized.
     *
     * @see get
     */
    @Throws(NotInitializeException::class)
    fun <T : Any> getOrNull(type: KClass<T>): T? = KJectImpl.getOrNull(type)

    /**
     * @throws NotInitializeException If KJect is not initialized.
     *
     * @see getOrNull
     */
    @Throws(NotInitializeException::class)
    inline fun <reified T : Any> getOrNull(): T? = getOrNull(T::class)

    /**
     * Gets an instance of the given [type] or creates it if it is not in the registry.
     *
     * @throws NotInitializeException If KJect is not initialized.
     * @throws IllegalFacadeException If [type] is annotated with [Facade][me.kject.annotation.Facade] but the building does not implement [type].
     * @throws CircularDependencyException If a circular dependency is detected.
     * @throws IllegalConstructorsException If multiple constructors are annotated with [UseConstructor][me.kject.annotation.UseConstructor] or no constructor is annotated and no primary constructor exists.
     * @throws MultipleFacadesException If multiple facades are found on the given [type].
     * @throws BadParameterException If a parameter of the constructor or the initialize function is not found.
     * @throws MultipleWithsException If multiple [With][me.kject.annotation.With] annotations are found on a function.
     * @throws CallCanceledException If the call to the constructor or the initialize function is canceled.
     * @throws CallFailedException If the call to the constructor or the initialize function fails.
     */
    @Throws(
        NotInitializeException::class,
        IllegalFacadeException::class,
        CircularDependencyException::class,
        IllegalConstructorsException::class,
        MultipleFacadesException::class,
        BadParameterException::class,
        MultipleWithsException::class,
        CallCanceledException::class,
        CallFailedException::class,
    )
    suspend fun <T : Any> getOrCreate(type: KClass<T>): T = KJectImpl.getOrCreate(type)

    /**
     * @throws NotInitializeException If KJect is not initialized.
     * @throws IllegalFacadeException If [type][T] is annotated with [Facade][me.kject.annotation.Facade] but the building does not implement [type][T].
     * @throws CircularDependencyException If a circular dependency is detected.
     * @throws IllegalConstructorsException If multiple constructors are annotated with [UseConstructor][me.kject.annotation.UseConstructor] or no constructor is annotated and no primary constructor exists.
     * @throws MultipleFacadesException If multiple facades are found on the given [type][T].
     * @throws BadParameterException If a parameter of the constructor or the initialize function is not found.
     * @throws MultipleWithsException If multiple [With][me.kject.annotation.With] annotations are found on a function.
     * @throws CallCanceledException If the call to the constructor or the initialize function is canceled.
     * @throws CallFailedException If the call to the constructor or the initialize function fails.
     *
     * @see getOrCreate
     */
    @Throws(
        NotInitializeException::class,
        IllegalFacadeException::class,
        CircularDependencyException::class,
        IllegalConstructorsException::class,
        MultipleFacadesException::class,
        BadParameterException::class,
        MultipleWithsException::class,
        CallCanceledException::class,
        CallFailedException::class,
    )
    suspend inline fun <reified T : Any> getOrCreate(): T = getOrCreate(T::class)

    /**
     * Creates an instance of the given [type] and saves it.
     *
     * First KJect will check if [type] is annotated with [Facade][me.kject.annotation.Facade].
     * If this is the case, the create function will be called with the building instead.
     * If the building doesn't implement [type] an [IllegalFacadeException] will be thrown.
     *
     * Then each [Require][me.kject.annotation.Require] annotation will be checked and it KJect ensures,
     * that either an instance exits that matches the required type or tires to create one.
     *
     * If [type] is an object, the object instance will now be used, otherwise an instance needs to be created.
     * For this KJect tires to find a constructor annotated with [UseConstructor][me.kject.annotation.UseConstructor].
     * If multiple constructors that match are found an [IllegalConstructorsException] will be thrown.
     * If no constructor is annotated, the primary constructor will be used.
     * Otherwise, an [IllegalConstructorsException] will be thrown.
     *
     * Now the created instance will be saved for later use.
     *
     * After that KJect will search for any [Initialize][me.kject.annotation.Initialize] annotation on a function
     * of the given [type] and call it. If multiple function are annotated,
     * all of them will be called in no specific order.
     *
     * After that the created instance will be returned.
     *
     * @throws NotInitializeException If KJect is not initialized.
     * @throws IllegalFacadeException If [type] is annotated with [Facade][me.kject.annotation.Facade] but the building does not implement [type].
     * @throws CircularDependencyException If a circular dependency is detected.
     * @throws IllegalConstructorsException If multiple constructors are annotated with [UseConstructor][me.kject.annotation.UseConstructor] or no constructor is annotated and no primary constructor exists.
     * @throws MultipleFacadesException If multiple facades are found on the given [type].
     * @throws BadParameterException If a parameter of the constructor or the initialize function is not found.
     * @throws MultipleWithsException If multiple [With][me.kject.annotation.With] annotations are found on a function.
     * @throws CallCanceledException If the call to the constructor or the initialize function is canceled.
     * @throws CallFailedException If the call to the constructor or the initialize function fails.
     */
    @Throws(
        NotInitializeException::class,
        IllegalFacadeException::class,
        CircularDependencyException::class,
        IllegalConstructorsException::class,
        MultipleFacadesException::class,
        BadParameterException::class,
        MultipleWithsException::class,
        CallCanceledException::class,
        CallFailedException::class,
    )
    suspend fun <T : Any> create(type: KClass<T>): T = KJectImpl.create(type)

    /**
     * @throws NotInitializeException If KJect is not initialized.
     * @throws IllegalFacadeException If [type][T] is annotated with [Facade][me.kject.annotation.Facade] but the building does not implement [type][T].
     * @throws CircularDependencyException If a circular dependency is detected.
     * @throws IllegalConstructorsException If multiple constructors are annotated with [UseConstructor][me.kject.annotation.UseConstructor] or no constructor is annotated and no primary constructor exists.
     * @throws MultipleFacadesException If multiple facades are found on the given [type][T].
     * @throws BadParameterException If a parameter of the constructor or the initialize function is not found.
     * @throws MultipleWithsException If multiple [With][me.kject.annotation.With] annotations are found on a function.
     * @throws CallCanceledException If the call to the constructor or the initialize function is canceled.
     * @throws CallFailedException If the call to the constructor or the initialize function fails.
     *
     * @see create
     */
    @Throws(
        NotInitializeException::class,
        IllegalFacadeException::class,
        CircularDependencyException::class,
        IllegalConstructorsException::class,
        MultipleFacadesException::class,
        BadParameterException::class,
        MultipleWithsException::class,
        CallCanceledException::class,
        CallFailedException::class,
    )
    suspend inline fun <reified T : Any> create(): T = create(T::class)

    /**
     * Calls the [function] with the parameters from the [builder].
     *
     * Any parameter on the function annotated with [Inject][me.kject.annotation.Inject]
     * will be replaced with an instance gotten from [getOrCreate].
     *
     * Any other parameter will be gotten either from the builder, passed as null or passed as default value.
     * If a parameter is not present in the builder, has no default value and is not nullable, an
     * [BadParameterException] will be thrown.
     *
     * A [Deferred] is returned, that will be completed
     * - with the result of the function
     * - an [CallFailedException] if the called function throws an exception or
     * - an [CallCanceledException] if KJect was disposed while the function was running.
     *
     * If the invoked function is suspending, the call is also suspending.
     * See [Tactic][me.kject.annotation.With.Tactic] for more information on how suspending functions are called.
     *
     * @throws NotInitializeException If KJect is not initialized.
     * @throws IllegalFacadeException If any type is annotated with [Facade][me.kject.annotation.Facade] but the building does not implement [type][T].
     * @throws CircularDependencyException If a circular dependency is detected.
     * @throws IllegalConstructorsException If multiple constructors are annotated with [UseConstructor][me.kject.annotation.UseConstructor].
     * @throws MultipleFacadesException If multiple facades are found on any type.
     * @throws BadParameterException If a parameter of the constructor or the initialize function is not found.
     * @throws MultipleWithsException If multiple [With][me.kject.annotation.With] annotations are found on a function.
     *
     * @see getOrCreate
     */
    @Throws(
        NotInitializeException::class,
        IllegalFacadeException::class,
        CircularDependencyException::class,
        IllegalConstructorsException::class,
        MultipleFacadesException::class,
        BadParameterException::class,
        MultipleWithsException::class,
    )
    suspend fun <T> call(function: KFunction<T>, builder: CallBuilder<T>.() -> Unit = {}): Deferred<T> =
        KJectImpl.call(function, builder)


}