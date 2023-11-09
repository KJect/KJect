package me.kject.test.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking

object Scope : CoroutineScope {

    override val coroutineContext = Job()

}

fun blocking(block: suspend CoroutineScope.() -> Unit) = runBlocking(Scope.coroutineContext, block)