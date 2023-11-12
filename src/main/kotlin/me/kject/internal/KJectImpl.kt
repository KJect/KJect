package me.kject.internal

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.kject.call.CallBuilder
import me.kject.dependency.trace.DependencyTraceBuilder
import me.kject.dependency.trace.FunctionElement
import me.kject.exception.AlreadyInitializedException
import me.kject.exception.NotFoundException
import me.kject.exception.NotInitializedException
import me.kject.internal.call.Caller
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

internal object KJectImpl {

    private val mutex = Mutex()

    var disposed: Boolean = true
        private set

    private var _scope: CoroutineScope? = null
    private var _context: String? = null

    val scope: CoroutineScope
        get() = _scope ?: throw NotInitializedException()

    val context: String
        get() = _context ?: throw NotInitializedException()

    suspend fun launch(scope: CoroutineScope, context: String) {
        mutex.withLock {
            if (!disposed || _scope != null || _context != null) throw AlreadyInitializedException()
            disposed = false

            _scope = CoroutineScope(
                EmptyCoroutineContext +
                        Job(scope.coroutineContext.job) +
                        CoroutineName("KJect")
            )
            _context = context

            Registry.allowCreate = true
        }
    }

    suspend fun dispose() {
        mutex.withLock {
            if (disposed || _scope == null || _context == null) throw NotInitializedException()
            disposed = true

            Registry.allowCreate = false

            scope.coroutineContext.cancelChildren()

            try {
                Registry.disposeInstances()
            } finally {
                Registry.clear()

                scope.cancel()

                _scope = null
                _context = null
            }
        }
    }

    operator fun <T : Any> get(type: KClass<T>): T {
        if (disposed) throw NotInitializedException()
        return Registry[type] ?: throw NotFoundException(type)
    }

    fun <T : Any> getOrNull(type: KClass<T>): T? {
        if (disposed) throw NotInitializedException()
        return Registry[type]
    }

    suspend fun <T : Any> getOrCreate(type: KClass<T>): T {
        if (disposed) throw NotInitializedException()
        return Registry[type] ?: Registry.create(type, DependencyTraceBuilder.create())
    }

    suspend fun <T : Any> create(type: KClass<T>): T {
        if (disposed) throw NotInitializedException()
        return Registry.create(type, DependencyTraceBuilder.create())
    }

    suspend fun <T> call(function: KFunction<T>, builder: CallBuilder<T>.() -> Unit): Deferred<T> {
        if (disposed) throw NotInitializedException()
        return Caller.call(function, builder, DependencyTraceBuilder.create().also { it += FunctionElement(function) })
    }

}