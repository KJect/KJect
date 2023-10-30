package me.kject.internal.call

import kotlinx.coroutines.Deferred
import me.kject.call.CallBuilder
import me.kject.dependency.trace.DependencyTraceBuilder
import kotlin.reflect.KFunction

object Caller {

    suspend fun <T> call(
        function: KFunction<T>,
        builder: CallBuilder<T>.() -> Unit,
        dependencyTraceBuilder: DependencyTraceBuilder
    ) : Deferred<T> {
        TODO()
    }

}