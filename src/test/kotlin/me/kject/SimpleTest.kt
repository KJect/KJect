package me.kject

import me.kject.exception.NotFoundException
import me.kject.util.KJectTest
import me.kject.util.TestScope
import me.kject.util.blocking
import org.junit.jupiter.api.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class Simple
object SimpleObject

class SimpleTest : KJectTest() {

    @Test
    @Order(1)
    fun testIllegalGet() {
        assertNull(KJect.getOrNull<Simple>())
        assertThrows<NotFoundException> { KJect.get<Simple>() }
    }

    @Test
    @Order(2)
    fun testCreate() {
        val instance = assertDoesNotThrow {
            blocking { KJect.create<Simple>() }
        }

        assertEquals(Simple::class, instance::class)
        assertEquals(instance, KJect.get<Simple>())

        blocking {
            assertEquals(instance, KJect.getOrCreate<Simple>())
        }
    }

    @Test
    @Order(3)
    fun testObject() {
        val instance = assertDoesNotThrow {
            blocking { KJect.create<SimpleObject>() }
        }

        assertEquals(SimpleObject::class, instance::class)
        assertEquals(instance, KJect.get<SimpleObject>())
        assertEquals(instance, SimpleObject)

        blocking {
            assertEquals(instance, KJect.getOrCreate<SimpleObject>())
        }
    }

}