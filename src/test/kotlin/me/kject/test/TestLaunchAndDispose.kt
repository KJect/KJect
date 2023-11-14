package me.kject.test

import me.kject.KJect
import me.kject.exception.AlreadyInitializedException
import me.kject.exception.NotInitializedException
import me.kject.util.Scope
import me.kject.util.assertDoesNotThrow
import me.kject.util.assertThrows
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.Test

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TestLaunchAndDispose {

    @Test
    @Order(1)
    fun testLaunch() {
        assertDoesNotThrow { KJect.launch(Scope) }
        assertThrows<AlreadyInitializedException> { KJect.launch(Scope) }
    }

    @Test
    @Order(2)
    fun testDispose() {
        assertDoesNotThrow { KJect.dispose() }
        assertThrows<NotInitializedException> { KJect.dispose() }
    }

    @Test
    @Order(3)
    fun testNotInitialized() {
        assertThrows<NotInitializedException> { KJect.get<Any>() }
        assertThrows<NotInitializedException> { KJect.getOrNull<Any>() }
        assertThrows<NotInitializedException> { KJect.getOrCreate<Any>() }
        assertThrows<NotInitializedException> { KJect.create<Any>() }
        assertThrows<NotInitializedException> {
            @Suppress("DeferredResultUnused")
            KJect.call(::testNotInitialized)
        }
    }

}