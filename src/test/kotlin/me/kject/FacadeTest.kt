package me.kject

import me.kject.annotation.Facade
import me.kject.exception.create.IllegalFacadeException
import me.kject.util.KJectTest
import me.kject.util.TestScope
import me.kject.util.blocking
import org.junit.jupiter.api.*
import kotlin.test.assertEquals

@Facade(BuildingA::class)
interface FacadeA
class BuildingA : FacadeA

@Facade(BuildingB::class)
interface FacadeB
object BuildingB : FacadeB

@Facade(BuildingC::class)
interface FacadeC
class BuildingC

class FacadeTest : KJectTest() {

    @Test
    fun testFacadeA() {
        val instance = assertDoesNotThrow {
            blocking { KJect.create<FacadeA>() }
        }

        assertEquals(BuildingA::class, instance::class)
        assertEquals(instance, KJect.get<FacadeA>())
        assertEquals(instance, KJect.get<BuildingA>())

        blocking {
            assertEquals(instance, KJect.getOrCreate<FacadeA>())
            assertEquals(instance, KJect.getOrCreate<BuildingA>())
        }
    }

    @Test
    fun testFacadeB() {
        val instance = assertDoesNotThrow {
            blocking { KJect.create<FacadeB>() }
        }

        assertEquals(BuildingB::class, instance::class)
        assertEquals(instance, KJect.get<FacadeB>())
        assertEquals(instance, KJect.get<BuildingB>())
        assertEquals(instance, BuildingB)

        blocking {
            assertEquals(instance, KJect.getOrCreate<FacadeB>())
            assertEquals(instance, KJect.getOrCreate<BuildingB>())
        }
    }

    @Test
    fun testFacadeC() {
        assertThrows<IllegalFacadeException> {
            blocking { KJect.create<FacadeC>() }
        }
    }

}