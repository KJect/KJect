package me.kject.test

import me.kject.KJect
import me.kject.exception.NotFoundException
import me.kject.util.KJectTest
import me.kject.util.assertDoesNotThrow
import me.kject.util.assertThrows
import me.kject.util.blocking
import org.junit.jupiter.api.Order
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TestStandard : KJectTest() {

    @Test
    @Order(1)
    fun testNotFound() {
        assertThrows<NotFoundException> { KJect.get<Instance>() }
        assertEquals(null, KJect.getOrNull<Instance>())
    }

    @Test
    @Order(2)
    fun testCreateAndGet() {
        val instance = assertDoesNotThrow { KJect.create<Instance>() }
        assertIs<Instance>(instance)

        assertEquals(instance, KJect.get<Instance>())
        assertEquals(instance, KJect.getOrNull<Instance>())
        blocking { assertEquals(instance, KJect.getOrCreate<Instance>()) }
    }

    private class Instance

}
