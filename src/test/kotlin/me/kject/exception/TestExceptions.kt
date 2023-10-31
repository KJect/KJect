package me.kject.exception

import me.kject.KJect
import me.kject.annotation.Initialize
import me.kject.annotation.Inject
import me.kject.annotation.Require
import me.kject.exception.create.CircularDependencyException
import me.kject.util.KJectTest
import me.kject.util.blocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows


class TestExceptions : KJectTest() {

    @Test
    fun testNotFoundException() {
        assertThrows<NotFoundException> { KJect.get<ExampleA>() }
    }

    @Test
    fun testCircularDependencyException() {
        assertThrows<CircularDependencyException> {
            blocking { KJect.create<ExampleB>() }
        }

        assertThrows<CircularDependencyException> {
            blocking { KJect.create<ExampleD>() }
        }

        assertThrows<CircularDependencyException> {
            blocking { KJect.create<ExampleF>() }
        }
    }


    class ExampleA

    @Require(ExampleC::class)
    class ExampleB

    @Require(ExampleB::class)
    class ExampleC
    class ExampleD(@Inject val e: ExampleE)
    class ExampleE(@Inject val d: ExampleD)
    class ExampleF {

        @Initialize
        fun initialize(@Inject g: ExampleG) {
            println(g)
        }

    }
    class ExampleG {

        @Initialize
        fun initialize(@Inject f: ExampleF) {}

    }
    class ExampleH
    class ExampleI
    class ExampleJ

}