package me.kject.test

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.kject.KJect
import me.kject.annotation.Dispose
import me.kject.annotation.UseConstructor
import me.kject.exception.NotInitializeException
import me.kject.exception.create.IllegalConstructorsException
import me.kject.test.util.KJectTest
import me.kject.test.util.assertThrows
import me.kject.test.util.blocking
import org.junit.jupiter.api.Order
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class TestCreateException : KJectTest(teardown = false) {

    @Test
    @Order(1)
    fun testIllegalConstructor() {
        assertThrows<IllegalConstructorsException> { KJect.create<NoConstructor>() }
        assertThrows<IllegalConstructorsException> { KJect.create<MultipleConstructors>() }
    }

    @Test
    @Order(2)
    fun testInDispose() {
        blocking { KJect.create<BlockDispose>() }

        blocking {
            launch { KJect.dispose() }
            assertThrows<NotInitializeException> { KJect.create<Instance>() }
        }
    }

    private class NoConstructor {

        @Suppress("ConvertSecondaryConstructorToPrimary", "unused")
        private constructor()

    }

    private class MultipleConstructors {

        @UseConstructor
        @Suppress("unused")
        private constructor()

        @UseConstructor
        @Suppress("unused", "UNUSED_PARAMETER")
        private constructor(a: Int)

    }

    private class BlockDispose {

        @Dispose
        suspend fun dispose() {
            delay(1.seconds)
        }

    }

    private class Instance

}