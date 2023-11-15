package me.kject.test

import me.kject.KJect
import me.kject.annotation.Initialize
import me.kject.util.KJectTest
import me.kject.util.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals

class TestObject : KJectTest() {

    @Test
    fun testObject() {
        assertDoesNotThrow { KJect.create<Object>() }

        assertEquals(Object, KJect.get<Object>())
        assertEquals("Hello World!", Object.value)
    }

    internal object Object {

        lateinit var value: String
            private set

        @Initialize
        fun initialize() {
            value = "Hello World!"
        }

    }

}