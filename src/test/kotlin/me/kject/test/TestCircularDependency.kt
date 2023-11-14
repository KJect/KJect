package me.kject.test

import me.kject.KJect
import me.kject.annotation.Initialize
import me.kject.annotation.Inject
import me.kject.annotation.Require
import me.kject.dependency.trace.ClassElement
import me.kject.dependency.trace.FunctionElement
import me.kject.dependency.trace.RequestType
import me.kject.exception.create.CircularDependencyException
import me.kject.util.KJectTest
import me.kject.util.assertThrows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class TestCircularDependency : KJectTest() {

    @Test
    fun testAnnotation() {
        val exception = assertThrows<CircularDependencyException> { KJect.create<Annotation.A>() }
        assertEquals(3, exception.dependencyTrace.elements.size)
        exception.dependencyTrace.elements
            .onEach { assertIs<ClassElement>(it) }
            .filterIsInstance<ClassElement>()
            .toMutableList()
            .also { assertNull(it.removeFirst().through) }
            .forEach { assertEquals(RequestType.REQUIRE, it.through) }
    }

    @Test
    fun testConstructor() {
        val exception = assertThrows<CircularDependencyException> { KJect.create<Constructor.A>() }
        assertEquals(3, exception.dependencyTrace.elements.size)
        exception.dependencyTrace.elements
            .onEach { assertIs<ClassElement>(it) }
            .filterIsInstance<ClassElement>()
            .toMutableList()
            .also { assertNull(it.removeFirst().through) }
            .forEach { assertEquals(RequestType.CONSTRUCTOR, it.through) }
    }

    @Test
    fun testInitializeInject() {
        val exception = assertThrows<CircularDependencyException> { KJect.create<InitializeInject.A>() }
        assertEquals(3, exception.dependencyTrace.elements.size)
        exception.dependencyTrace.elements
            .onEach { assertIs<ClassElement>(it) }
            .filterIsInstance<ClassElement>()
            .toMutableList()
            .also { assertNull(it.removeFirst().through) }
            .forEach { assertEquals(RequestType.INITIALIZE, it.through) }
    }

    @Test
    fun testWithMethod() {
        val exception = assertThrows<CircularDependencyException> {
            KJect.call(::inject)
        }
        assertEquals(4, exception.dependencyTrace.elements.size)
        exception.dependencyTrace.elements
            .toMutableList()
            .also { assertIs<FunctionElement>(it.removeFirst()) }
            .onEach { assertIs<ClassElement>(it) }
            .filterIsInstance<ClassElement>()
            .toMutableList()
            .also { assertNull(it.removeFirst().through) }
            .forEach { assertEquals(RequestType.REQUIRE, it.through) }
    }

    private class Annotation {

        @Require(B::class)
        class A

        @Require(A::class)
        class B

    }

    private class Constructor {

        class A(@Inject val b: B)

        class B(@Inject val a: A)

    }

    private class InitializeInject {

        class A {

            @Initialize
            fun initialize(@Inject b: B) {
            }


        }

        class B {

            @Initialize
            fun initialize(@Inject a: A) {
            }

        }

    }

    private fun inject(@Inject a: Annotation.A) = Unit

}
