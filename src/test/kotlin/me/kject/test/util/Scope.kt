package me.kject.test.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking

object Scope : CoroutineScope {

    override val coroutineContext = Job()

}

fun <R> blocking(block: suspend CoroutineScope.() -> R): R = runBlocking(Scope.coroutineContext, block)