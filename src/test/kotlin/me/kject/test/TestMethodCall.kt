package me.kject.test

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.kject.KJect
import me.kject.annotation.Inject
import me.kject.annotation.With
import me.kject.exception.call.BadParameterException
import me.kject.exception.call.CallCanceledException
import me.kject.exception.call.CallFailedException
import me.kject.exception.call.MultipleWithsException
import me.kject.exception.parameter.NoInstanceParameterException
import me.kject.exception.parameter.NoReceiverParameterException
import me.kject.exception.parameter.UnknownParameterException
import me.kject.util.KJectTest
import me.kject.util.Scope
import me.kject.util.assertThrows
import me.kject.util.blocking
import org.junit.jupiter.api.Order
import kotlin.test.Test
import kotlin.test.assertEquals

class TestMethodCall : KJectTest(teardown = false) {

    @Test
    @Order(1)
    fun testWith() {
        assertEquals(true, blocking { KJect.call(::methodA).await() })
        println(blocking { KJect.call(::methodB).await().startsWith("DefaultDispatcher-worker-1") })
    }

    @Test
    @Order(2)
    fun testArgumentBuilder() {
        assertEquals("Hello World", blocking {
            KJect.call(String::something) {
                receiver = "Hello "
                this["other"] = "World"
            }.await()
        })

        val testClass = TestClass("World")
        assertEquals("Hello World", blocking {
            KJect.call(TestClass::test) {
                instance = testClass
            }.await()
        })
    }

    @Test
    @Order(3)
    fun testExceptions() {
        assertThrows<NoInstanceParameterException> { KJect.call(::test) { instance = "Hello" } }
        assertThrows<NoReceiverParameterException> { KJect.call(::test) { receiver = "Hello" } }
        assertThrows<UnknownParameterException> { KJect.call(::test) { this["a"] = "Hello" } }
        assertThrows<BadParameterException> { KJect.call(::test) }
        assertThrows<CallFailedException> { KJect.call(::fail).await() }
        assertThrows<MultipleWithsException> { KJect.call(::multipleWith) }
    }

    @Test
    @Order(4)
    fun testCallCanceled() {
        blocking { KJect.dispose() }

        assertThrows<CallCanceledException> {
            KJect.launch(Scope)

            launch {
                delay(1000)
                KJect.dispose()
            }

            KJect.call(::wait).await()
        }
    }


}

fun main() {
    runBlocking {
        KJect.launch(this)

        launch {
            delay(1000)
            KJect.dispose()
        }

        KJect.call(::wait).await()
    }
}

private class InstanceA

@Suppress("UNUSED_PARAMETER")
private fun methodA(@Inject a: InstanceA): Boolean {
    return true
}

@With(With.Tactic.IO)
private fun methodB() = Thread.currentThread().name

private fun String.something(other: String) = this + other

private class TestClass(val parameter: String) {

    fun test() = "Hello $parameter"

}

private fun test(parameter: String) = "Hello $parameter"

private fun fail(): Nothing = throw Exception()

@With(With.Tactic.IO)
@With(With.Tactic.DEFAULT)
private fun multipleWith() = Unit

private suspend fun wait() {
    delay(2000)
}