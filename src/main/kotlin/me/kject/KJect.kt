package me.kject

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import me.kject.call.CallBuilder
import me.kject.exception.AlreadyInitializedException
import me.kject.exception.DisposeFailedException
import me.kject.exception.NotFoundException
import me.kject.exception.NotInitializedException
import me.kject.exception.call.BadParameterException
import me.kject.exception.call.CallCanceledException
import me.kject.exception.call.CallFailedException
import me.kject.exception.call.MultipleOnException
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
     * The [scope] is used to start jobs for calling functions
     * with different [Dispatchers][me.kject.annotation.On.Dispatcher]s.
     *
     * The [scope] **must not be canceled**, until KJect is disposed.
     * If the [scope] is canceled either way, a `CancellationException` will be thrown.
     *
     * @throws AlreadyInitializedException If KJect is already initialized.
     */
    @Throws(AlreadyInitializedException::class)
    suspend fun launch(scope: CoroutineScope, context: String = "production"): Unit = KJectImpl.launch(scope, context)

    /**
     * Disposes KJect.
     *
     * This will cancel all active calls currently running and afterward call all function
     * annotated with [Dispose][me.kject.annotation.Dispose] in any registered instances.
     *
     * While calling those functions, KJect ensures, that other instances required through the
     * [Require][me.kject.annotation.Require] annotation are still present.
     *
     * @throws NotInitializedException If KJect is not initialized.
     * @throws DisposeFailedException If KJect fails to dispose some instances.
     */
    @Throws(NotInitializedException::class, DisposeFailedException::class)
    suspend fun dispose(): Unit = KJectImpl.dispose()

    /**
     * Gets an instance of the given [type].
     *
     * Any instance ever created that matches the given [type] will be returned.
     * For this the returned instance does not have to be the exact same as the given [type],
     * but can also be a subclass of the given [type].
     *
     * @throws NotInitializedException If KJect is not initialized.
     * @throws NotFoundException If no instance of the given [type] is in the registry.
     */
    @Throws(NotInitializedException::class, NotFoundException::class)
    operator fun <T : Any> get(type: KClass<T>): T = KJectImpl[type]

    /**
     * Gets an instance of the given [type][T].
     *
     * Any instance ever created that matches the given [type][T] will be returned.
     * For this the returned instance does not have to be the exact same as the given [type][T],
     * but can also be a subclass of the given [type][T].
     *
     * @throws NotInitializedException If KJect is not initialized.
     * @throws NotFoundException If no instance of the given [type][T] is in the registry.
     *
     * @see get
     */
    @Throws(NotInitializedException::class, NotFoundException::class)
    inline fun <reified T : Any> get(): T = get(T::class)

    /**
     * Gets an instance of the given [type] or `null` if no instance of the given [type] is in the registry.
     *
     * @throws NotInitializedException If KJect is not initialized.
     *
     * @see get
     */
    @Throws(NotInitializedException::class)
    fun <T : Any> getOrNull(type: KClass<T>): T? = KJectImpl.getOrNull(type)

    /**
     * Gets an instance of the given [type][T] or `null` if no instance of the given [type][T] is in the registry.
     *
     * @throws NotInitializedException If KJect is not initialized.
     *
     * @see getOrNull
     */
    @Throws(NotInitializedException::class)
    inline fun <reified T : Any> getOrNull(): T? = getOrNull(T::class)

    /**
     * Gets an instance of the given [type] or creates it if it is not in the registry.
     *
     * @throws NotInitializedException If KJect is not initialized.
     * @throws IllegalFacadeException If a class that should be created is annotated
     * with [Facade][me.kject.annotation.Facade] but the building does not implement the facade.
     * @throws CircularDependencyException If a circular dependency is detected.
     * @throws IllegalConstructorsException If multiple constructors are annotated
     * with [UseConstructor][me.kject.annotation.UseConstructor] or
     * no constructor is annotated and no primary constructor exists.
     * @throws MultipleFacadesException If multiple facades are found on a class.
     * @throws BadParameterException If a parameter of the constructor or the initialize function is not found.
     * @throws MultipleOnException If multiple [On][me.kject.annotation.On] annotations are found on a function.
     * @throws CallCanceledException If the call to a constructor or initializer function is canceled.
     * @throws CallFailedException If the call to a constructor or initializer function fails.
     *
     * @see get
     * @see create
     */
    @Throws(
        NotInitializedException::class,
        IllegalFacadeException::class,
        CircularDependencyException::class,
        IllegalConstructorsException::class,
        MultipleFacadesException::class,
        BadParameterException::class,
        MultipleOnException::class,
        CallCanceledException::class,
        CallFailedException::class,
    )
    suspend fun <T : Any> getOrCreate(type: KClass<T>): T = KJectImpl.getOrCreate(type)

    /**
     * Gets an instance of the given [type][T] or creates it if it is not in the registry.
     *
     * @throws NotInitializedException If KJect is not initialized.
     * @throws IllegalFacadeException If a class that should be created is annotated
     * with [Facade][me.kject.annotation.Facade] but the building does not implement the facade.
     * @throws CircularDependencyException If a circular dependency is detected.
     * @throws IllegalConstructorsException If multiple constructors are annotated
     * with [UseConstructor][me.kject.annotation.UseConstructor] or
     * no constructor is annotated and no primary constructor exists.
     * @throws MultipleFacadesException If multiple facades are found on a class.
     * @throws BadParameterException If a parameter of the constructor or the initialize function is not found.
     * @throws MultipleOnException If multiple [On][me.kject.annotation.On] annotations are found on a function.
     * @throws CallCanceledException If the call to a constructor or initializer function is canceled.
     * @throws CallFailedException If the call to a constructor or initializer function fails.
     *
     * @see getOrCreate
     */
    @Throws(
        NotInitializedException::class,
        IllegalFacadeException::class,
        CircularDependencyException::class,
        IllegalConstructorsException::class,
        MultipleFacadesException::class,
        BadParameterException::class,
        MultipleOnException::class,
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
     * Then each [Require][me.kject.annotation.Require] annotation will be checked and KJect ensures,
     * that either an instance exits that matches the required type or tires to create one.
     *
     * If [type] is an object, the object instance will now be used, otherwise an instance needs to be created.
     * For this KJect tires to find a constructor annotated with [UseConstructor][me.kject.annotation.UseConstructor].
     * If multiple constructors that match are found an [IllegalConstructorsException] will be thrown.
     * If no constructor is annotated, the primary constructor will be used.
     * If no primary constructor exists, an [IllegalConstructorsException] will be thrown.
     *
     * After that KJect will search for any [Initialize][me.kject.annotation.Initialize] annotation on a function
     * of the given [type] and call it. If multiple function are annotated,
     * all of them will be called in no specific order.
     * The completion of a suspending initialize function will be awaited.
     *
     * The created instance or object instance is then saved in the registry.
     *
     * After that the created instance will be returned.
     *
     * @throws NotInitializedException If KJect is not initialized.
     * @throws IllegalFacadeException If a class that should be created is annotated
     * with [Facade][me.kject.annotation.Facade] but the building does not implement the facade.
     * @throws CircularDependencyException If a circular dependency is detected.
     * @throws IllegalConstructorsException If multiple constructors are annotated
     * with [UseConstructor][me.kject.annotation.UseConstructor] or
     * no constructor is annotated and no primary constructor exists.
     * @throws MultipleFacadesException If multiple facades are found on a class.
     * @throws BadParameterException If a parameter of the constructor or the initialize function is not found.
     * @throws MultipleOnException If multiple [On][me.kject.annotation.On] annotations are found on a function.
     * @throws CallCanceledException If the call to a constructor or initializer function is canceled.
     * @throws CallFailedException If the call to a constructor or initializer function fails.
     */
    @Throws(
        NotInitializedException::class,
        IllegalFacadeException::class,
        CircularDependencyException::class,
        IllegalConstructorsException::class,
        MultipleFacadesException::class,
        BadParameterException::class,
        MultipleOnException::class,
        CallCanceledException::class,
        CallFailedException::class,
    )
    suspend fun <T : Any> create(type: KClass<T>): T = KJectImpl.create(type)

    /**
     * Creates an instance of the given [type][T] and saves it.
     *
     * First KJect will check if [type][T] is annotated with [Facade][me.kject.annotation.Facade].
     * If this is the case, the create function will be called with the building instead.
     * If the building doesn't implement [type][T] an [IllegalFacadeException] will be thrown.
     *
     * Then each [Require][me.kject.annotation.Require] annotation will be checked and KJect ensures,
     * that either an instance exits that matches the required type or tires to create one.
     *
     * If [type][T] is an object, the object instance will now be used, otherwise an instance needs to be created.
     * For this KJect tires to find a constructor annotated with [UseConstructor][me.kject.annotation.UseConstructor].
     * If multiple constructors that match are found an [IllegalConstructorsException] will be thrown.
     * If no constructor is annotated, the primary constructor will be used.
     * If no primary constructor exists, an [IllegalConstructorsException] will be thrown.
     *
     * After that KJect will search for any [Initialize][me.kject.annotation.Initialize] annotation on a function
     * of the given [type][T] and call it. If multiple function are annotated,
     * all of them will be called in no specific order.
     * The completion of a suspending initialize function will be awaited.
     *
     * The created instance or object instance is then saved in the registry.
     *
     * After that the created instance will be returned.
     *
     * @throws NotInitializedException If KJect is not initialized.
     * @throws IllegalFacadeException If a class that should be created is annotated
     * with [Facade][me.kject.annotation.Facade] but the building does not implement the facade.
     * @throws CircularDependencyException If a circular dependency is detected.
     * @throws IllegalConstructorsException If multiple constructors are annotated
     * with [UseConstructor][me.kject.annotation.UseConstructor] or
     * no constructor is annotated and no primary constructor exists.
     * @throws MultipleFacadesException If multiple facades are found on a class.
     * @throws BadParameterException If a parameter of the constructor or the initialize function is not found.
     * @throws MultipleOnException If multiple [On][me.kject.annotation.On] annotations are found on a function.
     * @throws CallCanceledException If the call to a constructor or initializer function is canceled.
     * @throws CallFailedException If the call to a constructor or initializer function fails.
     *
     * @see create
     */
    @Throws(
        NotInitializedException::class,
        IllegalFacadeException::class,
        CircularDependencyException::class,
        IllegalConstructorsException::class,
        MultipleFacadesException::class,
        BadParameterException::class,
        MultipleOnException::class,
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
     * Any other parameter will be gotten either from the builder, passed as null or as the default value.
     * If a parameter is not present in the builder, has no default value and is not nullable, an
     * [BadParameterException] will be thrown.
     *
     * A [Deferred] is returned, that will be completed
     * - with the result of the function,
     * - a [CallFailedException] if the called function threw an exception or
     * - a [CallCanceledException] if KJect was disposed while the function was running.
     *
     * All calls ar executed in a separate coroutine.
     * **NOTE:** The returned [Deferred] is not the job of that coroutine.
     *
     * See [On][me.kject.annotation.On] for more information on where suspending functions are called.
     *
     * To await the result of the [Deferred] use [Deferred.await] or [Deferred.get].
     *
     * @throws NotInitializedException If KJect is not initialized.
     * @throws IllegalFacadeException If a class that should be created is annotated
     * with [Facade][me.kject.annotation.Facade] but the building does not implement the facade.
     * @throws CircularDependencyException If a circular dependency is detected.
     * @throws IllegalConstructorsException If multiple constructors are annotated
     * with [UseConstructor][me.kject.annotation.UseConstructor] or
     * no constructor is annotated and no primary constructor exists.
     * @throws MultipleFacadesException If multiple facades are found on a class.
     * @throws BadParameterException If a parameter of the constructor or the initialize function is not found.
     * @throws MultipleOnException If multiple [On][me.kject.annotation.On] annotations are found on a function.
     * @throws CallCanceledException If the call to a constructor or initializer function is canceled.
     * @throws CallFailedException If the call to a constructor or initializer function fails.
     *
     * @see getOrCreate
     */
    @Throws(
        NotInitializedException::class,
        IllegalFacadeException::class,
        CircularDependencyException::class,
        IllegalConstructorsException::class,
        MultipleFacadesException::class,
        BadParameterException::class,
        MultipleOnException::class,
        CallCanceledException::class,
        CallFailedException::class,
    )
    suspend fun <T> call(function: KFunction<T>, builder: CallBuilder<T>.() -> Unit = {}): Deferred<T> =
        KJectImpl.call(function, builder)


}