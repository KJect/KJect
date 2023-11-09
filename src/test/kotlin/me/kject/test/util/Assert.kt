package me.kject.test.util

import kotlinx.coroutines.CoroutineScope

fun assertDoesNotThrow(block: suspend CoroutineScope.() -> Unit) = org.junit.jupiter.api.assertDoesNotThrow {
    blocking {
        block()
    }
}

inline fun <reified T : Throwable> assertThrows(noinline block: suspend CoroutineScope.() -> Unit) =
    org.junit.jupiter.api.assertThrows<T> {
        blocking {
            block()
        }
    }