package me.kject.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking

object TestScope : CoroutineScope {

    override val coroutineContext = Job()

}

fun <T> blocking(block: suspend CoroutineScope.() -> T) = runBlocking(TestScope.coroutineContext, block)