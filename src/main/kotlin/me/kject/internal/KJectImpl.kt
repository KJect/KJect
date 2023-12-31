package me.kject.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.kject.dependency.trace.DependencyTraceBuilder
import me.kject.exception.AlreadyInitializeException
import me.kject.exception.NotFoundException
import me.kject.exception.NotInitializeException
import kotlin.reflect.KClass

internal object KJectImpl {

    private val mutex = Mutex()

    private var disposed: Boolean = true

    private var _scope: CoroutineScope? = null
    private var _context: String? = null

    val scope: CoroutineScope
        get() = _scope ?: throw NotInitializeException()

    val context: String
        get() = _context ?: throw NotInitializeException()

    fun getContextValue(context: String): Int {
        if (context == this.context) return 2
        if (context == "*") return 1

        return 0
    }

    suspend fun launch(scope: CoroutineScope, context: String) {
        mutex.withLock {
            if (!disposed || _scope != null || _context != null) throw AlreadyInitializeException()
            disposed = false

            _scope = scope
            _context = context

            Coroutine.allowLaunch = true
        }
    }

    suspend fun dispose() {
        mutex.withLock {
            if (disposed || _scope == null || _context == null) throw NotInitializeException()
            disposed = true

            Coroutine.allowLaunch = false
            Coroutine.cancelAllJobs()

            // TODO: Dispose all instances

            _scope = null
            _context = null
        }
    }

    operator fun <T : Any> get(type: KClass<T>) = Registry[type] ?: throw NotFoundException(type)

    fun <T : Any> getOrNull(type: KClass<T>) = Registry[type]

    suspend fun <T : Any> getOrCreate(type: KClass<T>) = Registry[type] ?: Registry.create(type, DependencyTraceBuilder.create())

    suspend fun <T : Any> create(type: KClass<T>) = Registry.create(type, DependencyTraceBuilder.create())

}