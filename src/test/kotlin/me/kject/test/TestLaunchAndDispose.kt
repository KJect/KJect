package me.kject.test

import me.kject.KJect
import me.kject.exception.AlreadyInitializeException
import me.kject.exception.NotInitializeException
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
        assertThrows<AlreadyInitializeException> { KJect.launch(Scope) }
    }

    @Test
    @Order(2)
    fun testDispose() {
        assertDoesNotThrow { KJect.dispose() }
        assertThrows<NotInitializeException> { KJect.dispose() }
    }

    @Test
    @Order(3)
    fun testNotInitialized() {
        assertThrows<NotInitializeException> { KJect.get<Any>() }
        assertThrows<NotInitializeException> { KJect.getOrNull<Any>() }
        assertThrows<NotInitializeException> { KJect.getOrCreate<Any>() }
        assertThrows<NotInitializeException> { KJect.create<Any>() }
        assertThrows<NotInitializeException> { KJect.call(::testNotInitialized) }
    }

}