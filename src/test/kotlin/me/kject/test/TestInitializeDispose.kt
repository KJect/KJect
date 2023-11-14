package me.kject.test

import me.kject.KJect
import me.kject.annotation.Dispose
import me.kject.annotation.Initialize
import me.kject.util.KJectTest
import me.kject.util.blocking
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestInitializeDispose : KJectTest() {

    override val teardown = false

    companion object {

        var initialized = false
            private set

    }

    @Test
    @Order(1)
    fun testInitialize() {
        blocking { KJect.create<Instance>() }
        assertEquals(true, initialized)
    }

    @Test
    @Order(2)
    fun testDispose() {
        blocking { KJect.dispose() }
        assertEquals(false, initialized)
    }

    private class Instance {

        @Initialize
        fun initialize() {
            initialized = true
        }

        @Dispose
        fun dispose() {
            initialized = false
        }

    }

}