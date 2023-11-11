package me.kject.test

import me.kject.KJect
import me.kject.annotation.Require
import me.kject.test.util.KJectTest
import me.kject.test.util.assertDoesNotThrow
import org.junit.jupiter.api.Order
import kotlin.test.Test

class TestRequire : KJectTest() {

    @Test
    @Order(1)
    fun testRequire() {
        assertDoesNotThrow { KJect.create<InstanceA>() }
        assertDoesNotThrow { KJect.get<InstanceB>() }
    }

    @Require(InstanceB::class)
    private class InstanceA

    private class InstanceB

}