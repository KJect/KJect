package me.kject.test

import me.kject.KJect
import me.kject.annotation.Dispose
import me.kject.exception.DisposeFailedException
import me.kject.util.KJectTest
import me.kject.util.assertDoesNotThrow
import me.kject.util.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class TestDisposeFail : KJectTest(teardown = false) {

    @Test
    fun testDisposeFail() {
        assertDoesNotThrow { KJect.create<Instance>() }
        val exception = assertThrows<DisposeFailedException> { KJect.dispose() }

        assertEquals(1, exception.instances.size)
    }

    private class Instance {

        var fail = true

        @Dispose
        fun dispose() {
            if (fail) throw Exception()
        }

    }

}