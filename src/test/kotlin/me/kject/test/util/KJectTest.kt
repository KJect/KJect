package me.kject.test.util

import me.kject.KJect
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.Test

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
open class KJectTest(
    private val context: String = "production",
    private val setup: Boolean = true,
    private val teardown: Boolean = true
) {

    @Test
    @Order(Int.MIN_VALUE)
    fun setup() = blocking {
        if (setup) KJect.launch(Scope, context)
    }

    @Test
    @Order(Int.MAX_VALUE)
    fun teardown() = blocking {
        if (teardown) KJect.dispose()
    }

}