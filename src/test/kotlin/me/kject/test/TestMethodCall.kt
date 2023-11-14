package me.kject.test

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.kject.KJect
import me.kject.annotation.Inject
import me.kject.annotation.On
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
        assert(blocking { KJect.call(::methodB).await().startsWith("DefaultDispatcher-worker-1 @KJect") })
        assert(blocking { KJect.call(::methodC).await().startsWith("Test worker @KJect") })
        println(blocking { KJect.call(::methodD).await().startsWith("kotlinx.coroutines.DefaultExecutor @KJect") })
        println(blocking { KJect.call(::methodE).await().startsWith("DefaultDispatcher-worker-1 @KJect") })
    }

    @Test
    @Order(2)
    fun testArgumentBuilder() {
        assertEquals("Hello World", blocking {
            KJect.call(String::something) {
                receiver = "Hello "
                this["other"] = "World"

                assertEquals("Hello ", receiver)
                assertEquals("World", this["other"])
            }.await()
        })

        val testClass = TestClass("World")
        assertEquals("Hello World", blocking {
            KJect.call(TestClass::test) {
                instance = testClass

                assertEquals(testClass, instance)
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

private class InstanceA

@Suppress("UNUSED_PARAMETER")
private fun methodA(@Inject a: InstanceA): Boolean {
    return true
}

@On(On.Dispatcher.DEFAULT)
private fun methodB() = Thread.currentThread().name

@On(On.Dispatcher.UNCONFINED)
private fun methodC() = Thread.currentThread().name

@On(On.Dispatcher.UNCONFINED)
private suspend fun methodD(): String {
    delay(1)
    return Thread.currentThread().name
}

@On(On.Dispatcher.IO)
private fun methodE() = Thread.currentThread().name

private fun String.something(other: String) = this + other

private class TestClass(val parameter: String) {

    fun test() = "Hello $parameter"

}

private fun test(parameter: String) = "Hello $parameter"

private fun fail(): Nothing = throw Exception()

@On(On.Dispatcher.IO)
@On(On.Dispatcher.DEFAULT)
private fun multipleWith() = Unit

private suspend fun wait() {
    delay(2000)
}