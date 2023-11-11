package me.kject.test

import me.kject.KJect
import me.kject.annotation.UseConstructor
import me.kject.test.util.KJectTest
import me.kject.test.util.assertDoesNotThrow
import org.junit.jupiter.api.Order
import kotlin.test.Test
import kotlin.test.assertEquals

class TestUseConstructor : KJectTest() {

    @Test
    @Order(1)
    fun testUseConstructor() {
        assertDoesNotThrow { KJect.create<InstanceA>() }
        assertEquals(1, KJect.get<InstanceA>().a)
    }

    private class InstanceA {

        val a: Int

        @UseConstructor
        constructor() {
            a = 1
        }

        @Suppress("unused")
        constructor(a: Int) {
            this.a = a
        }

    }

}