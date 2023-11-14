package me.kject.util

import kotlinx.coroutines.CancellationException
import me.kject.KJect
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.Test

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
open class KJectTest {

    open val context: String = "production"
    open  val setup: Boolean = true
    open  val teardown: Boolean = true

    @Test
    @Order(Int.MIN_VALUE)
    fun setup() = blocking {
        if (setup) KJect.launch(Scope, context)
    }

    @Test
    @Order(Int.MAX_VALUE)
    fun teardown() {
        try {
            blocking {
                if (teardown) KJect.dispose()
            }
        } catch (_: CancellationException) {
        }
    }


}