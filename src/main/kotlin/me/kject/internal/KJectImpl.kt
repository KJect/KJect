package me.kject.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.kject.exception.AlreadyInitializeException
import me.kject.exception.NotInitializeException

internal object KJectImpl {

    private val mutex = Mutex()

    private var disposed: Boolean = true

    private var _scope: CoroutineScope? = null
    private var _context: String? = null

    val scope: CoroutineScope
        get() = _scope ?: throw NotInitializeException()

    val context: String
        get() = _context ?: throw NotInitializeException()

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

}