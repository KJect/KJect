package me.kject.test

import me.kject.KJect
import me.kject.annotation.Facade
import me.kject.exception.create.IllegalFacadeException
import me.kject.exception.create.MultipleFacadesException
import me.kject.test.util.KJectTest
import me.kject.test.util.assertDoesNotThrow
import me.kject.test.util.assertThrows
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TestFacade : KJectTest() {

    @Test
    @Order(1)
    fun testSimpleFacade() {
        val instance = assertDoesNotThrow { KJect.create<FacadeA>() }
        assertIs<ImplementationA>(instance)

        assertEquals(instance, KJect.get<FacadeA>())
        assertEquals(instance, KJect.get<ImplementationA>())
    }

    @Test
    @Order(2)
    fun testBadFacade() {
        assertThrows<IllegalFacadeException> { KJect.create<FacadeB>() }
        assertThrows<MultipleFacadesException> { KJect.create<FacadeC>() }
    }

    @Test
    @Order(3)
    fun testComplexFacade() {
        val instance = assertDoesNotThrow { KJect.create<FacadeD1>() }
        assertIs<ImplementationD2>(instance)

        assertEquals(instance, KJect.get<FacadeD1>())
        assertEquals(instance, KJect.get<FacadeD2>())
        assertEquals(instance, KJect.get<ImplementationD2>())
    }

    @Facade(ImplementationA::class)
    private interface FacadeA
    private class ImplementationA : FacadeA

    @Facade(ImplementationB::class)
    private interface FacadeB
    private class ImplementationB

    @Facade(ImplementationC1::class)
    @Facade(ImplementationC2::class)
    private interface FacadeC

    private class ImplementationC1 : FacadeC
    private class ImplementationC2 : FacadeC

    @Facade(FacadeD2::class)
    private interface FacadeD1

    @Facade(ImplementationD2::class)
    private interface FacadeD2 : FacadeD1

    private class ImplementationD2 : FacadeD2

}