package me.kject.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import me.kject.exception.NotInitializeException
import kotlin.coroutines.CoroutineContext

object Coroutine {

    private val jobs = mutableListOf<Job>()

    var allowLaunch = false

    suspend fun cancelAllJobs() {
        jobs.forEach { it.cancel() }
        jobs.forEach { it.join() }

        jobs.clear()
    }

    fun <T> async(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> T,
        onDispose: Boolean = false,
    ): Deferred<T> {
        if (!allowLaunch && !onDispose) throw NotInitializeException()

        val deferred = KJectImpl.scope.async(context, block = block)

        if (!onDispose) {
            jobs.add(deferred)
            deferred.invokeOnCompletion { jobs.remove(deferred) }
        }

        return deferred
    }

}