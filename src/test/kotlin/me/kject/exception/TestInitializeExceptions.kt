package me.kject.exception

import me.kject.KJect
import me.kject.util.TestScope
import me.kject.util.blocking
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class TestInitializeExceptions {


    @Test
    @Order(1)
    fun testNotInitializedException() {
        assertThrows<NotInitializeException> {
            blocking {
                KJect.dispose()
            }
        }
    }

    @Test
    @Order(2)
    fun testAlreadyInitializedException() {
        assertDoesNotThrow {
            blocking {
                KJect.launch(TestScope)
            }
        }

        assertThrows<AlreadyInitializeException> {
            blocking {
                KJect.launch(TestScope)
            }
        }

        assertDoesNotThrow {
            blocking {
                KJect.dispose()
            }
        }
    }

}