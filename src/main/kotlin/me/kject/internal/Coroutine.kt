package me.kject.internal

import kotlinx.coroutines.*
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

    fun launch(
        context: CoroutineContext,
        onDispose: Boolean,
        block: suspend CoroutineScope.() -> Unit,
    ) {
        if (!allowLaunch && !onDispose) throw NotInitializeException()

        val job = KJectImpl.scope.launch(context, block = block)

        if (!onDispose) {
            jobs.add(job)
            job.invokeOnCompletion { jobs.remove(job) }
        }
    }

}