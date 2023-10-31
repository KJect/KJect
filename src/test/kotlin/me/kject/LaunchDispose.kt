package me.kject

import me.kject.exception.AlreadyInitializeException
import me.kject.exception.NotInitializeException
import me.kject.util.TestScope
import me.kject.util.blocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LaunchDispose {

    @Test
    fun testIllegalDispose() {
        assertThrows<NotInitializeException> {
            blocking { KJect.dispose() }
        }
    }

    @Test
    fun testIllegalLaunch() {
        assertThrows<AlreadyInitializeException> {
            blocking {
                KJect.launch(TestScope)
                KJect.launch(TestScope)
            }
        }

        blocking {
            KJect.dispose()
        }
    }

    @Test
    fun testIllegalAccess() {
        assertThrows<NotInitializeException> { KJect.get<Any>() }
        assertThrows<NotInitializeException> { KJect.getOrNull<Any>() }
        assertThrows<NotInitializeException> {
            blocking { KJect.getOrCreate<Any>() }
        }
        assertThrows<NotInitializeException> {
            blocking { KJect.create<Any>() }
        }
        assertThrows<NotInitializeException> {
            blocking {
                @Suppress("DeferredResultUnused")
                KJect.call(::testIllegalAccess)
            }
        }
    }

}