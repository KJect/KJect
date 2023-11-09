package me.kject.test

import me.kject.KJect
import me.kject.exception.NotFoundException
import me.kject.test.util.KJectTest
import me.kject.test.util.assertDoesNotThrow
import me.kject.test.util.assertThrows
import me.kject.test.util.blocking
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
