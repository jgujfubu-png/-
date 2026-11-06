package com.example

import com.example.math.RadicalEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCubeRootOf27() {
        val result = RadicalEngine.calculate(radicand = 27.0, index = 3.0, precision = 6)
        assertTrue(result.isDefined)
        assertFalse(result.isComplex)
        assertEquals("3", result.resultString)
    }

    @Test
    fun testOddRootOfNegativeNumber() {
        // Cube root of -8 should be real -2
        val result = RadicalEngine.calculate(radicand = -8.0, index = 3.0, precision = 6)
        assertTrue(result.isDefined)
        assertFalse(result.isComplex)
        assertTrue(result.isRealOddRoot)
        assertEquals("-2", result.resultString)
    }

    @Test
    fun testEvenRootOfNegativeNumberIsComplex() {
        // Square root of -16 should be 4i
        val result = RadicalEngine.calculate(radicand = -16.0, index = 2.0, precision = 6)
        assertTrue(result.isDefined)
        assertTrue(result.isComplex)
        assertEquals("4i", result.resultString)
    }

    @Test
    fun testFractionalIndex() {
        // 0.5-th root of 2 is 2^(1/0.5) = 2^2 = 4
        val result = RadicalEngine.calculate(radicand = 2.0, index = 0.5, precision = 6)
        assertTrue(result.isDefined)
        assertEquals("4", result.resultString)
    }

    @Test
    fun testNegativeIndex() {
        // -2-nd root of 100 is 100^(-1/2) = 0.1
        val result = RadicalEngine.calculate(radicand = 100.0, index = -2.0, precision = 6)
        assertTrue(result.isDefined)
        assertEquals("0.1", result.resultString)
    }

    @Test
    fun testZeroIndexIsUndefined() {
        val result = RadicalEngine.calculate(radicand = 25.0, index = 0.0, precision = 6)
        assertFalse(result.isDefined)
        assertEquals("Undefined", result.resultString)
    }

    @Test
    fun testZeroRadicand() {
        val positiveIndex = RadicalEngine.calculate(radicand = 0.0, index = 3.0)
        assertTrue(positiveIndex.isDefined)
        assertEquals("0", positiveIndex.resultString)

        val negativeIndex = RadicalEngine.calculate(radicand = 0.0, index = -3.0)
        assertFalse(negativeIndex.isDefined)
    }

    @Test
    fun testRadicalSimplifier() {
        // √72 = 6√2
        val simplified72 = RadicalEngine.simplifyRadical(radicand = 72L, index = 2)
        assertNotNull(simplified72)
        assertEquals(6L, simplified72?.outside)
        assertEquals(2L, simplified72?.inside)
        assertEquals("6√2", simplified72?.displayString())

        // ³√54 = 3³√2
        val simplified54 = RadicalEngine.simplifyRadical(radicand = 54L, index = 3)
        assertNotNull(simplified54)
        assertEquals(3L, simplified54?.outside)
        assertEquals(2L, simplified54?.inside)
        assertEquals("3 · 3√2", simplified54?.displayString())
    }

    @Test
    fun testAllRootsGeneration() {
        // 4th roots of 16 -> 4 roots, all with modulus 2
        val roots = RadicalEngine.generateAllRoots(radicand = 16.0, n = 4, precision = 4)
        assertEquals(4, roots.size)
    }
}
