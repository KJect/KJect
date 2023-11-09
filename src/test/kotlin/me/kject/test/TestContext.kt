package me.kject.test

import me.kject.KJect
import me.kject.annotation.Initialize
import me.kject.annotation.UseConstructor
import me.kject.test.util.KJectTest
import me.kject.test.util.Scope
import me.kject.test.util.assertDoesNotThrow
import me.kject.test.util.blocking
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestContext : KJectTest(setup = false, teardown = false) {

    @Test
    @Order(1)
    fun testContextA() {
        blocking { KJect.launch(Scope, "a") }
        assertDoesNotThrow { KJect.create<InstanceA>() }

        assertEquals("a", KJect.get<InstanceA>().a)

        blocking { KJect.dispose() }
    }

    @Test
    @Order(2)
    fun testContextB() {
        blocking { KJect.launch(Scope, "b") }
        assertDoesNotThrow { KJect.create<InstanceA>() }

        assertEquals("b", KJect.get<InstanceA>().a)

        blocking { KJect.dispose() }
    }

    private class InstanceA {

        lateinit var a: String
            private set

        @Initialize("a")
        fun initializeA() {
            a = "a"
        }

        @Initialize("b")
        fun initializeB() {
            a = "b"
        }

    }

}