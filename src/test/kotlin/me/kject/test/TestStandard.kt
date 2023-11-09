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

class TestStandard : KJectTest {

    @Test
    @Order(1)
    fun testNotFound() {
        assertThrows<NotFoundException> { KJect.get<InstanceA>() }
        assertEquals(null, KJect.getOrNull<InstanceA>())
    }

    @Test
    @Order(2)
    fun testCreateAndGet() {
        val instance = assertDoesNotThrow { KJect.create<InstanceA>() }
        assertEquals(InstanceA::class, instance::class)

        assertEquals(instance, KJect.get<InstanceA>())
        assertEquals(instance, KJect.getOrNull<InstanceA>())
        blocking { assertEquals(instance, KJect.getOrCreate<InstanceA>()) }
    }

}

private class InstanceA