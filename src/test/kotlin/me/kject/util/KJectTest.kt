package me.kject.util

import me.kject.KJect
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

open class KJectTest {

    companion object {

        @BeforeAll
        @JvmStatic
        fun setup() = blocking {
            KJect.launch(TestScope)
        }

        @AfterAll
        @JvmStatic
        fun teardown() = blocking {
            KJect.dispose()
        }

    }

}