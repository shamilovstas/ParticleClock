package com.shamilovstas.particleclock

import org.junit.Assert.*
import org.junit.Test

class AngleTest {

    @Test
    fun `should return angle 15 when angle 45 subtracted from angle 60`() {
        val angle60 = Angle(60f)
        val angle45 = Angle(45f)
        val expected = Angle(15f)
        val actual = angle60 - angle45
        assertEquals(expected, actual)
    }

    @Test
    fun `should return angle 355 when angle 10 subtracted from angle 5`() {
        val angle5 = Angle(5f)
        val angle10 = Angle(10f)
        val expected = Angle(355f)
        val actual = angle5 - angle10
        assertEquals(expected, actual)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw when angle is less than 0`() {
        Angle(-5f)
        fail()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw when angle is more than 360`() {
        Angle(540f)
        fail()
    }

    @Test
    fun `should return angle 40 when angle 30 added to angle 10`() {
        val angle30 = Angle(30f)
        val angle10 = Angle(10f)
        val expected = Angle(40f)
        val actual = angle30 + angle10
        assertEquals(expected, actual)
    }


    @Test
    fun `should return angle 10 when angle 330 added to angle 40`() {
        val angle330 = Angle(330f)
        val angle40 = Angle(40f)
        val expected = Angle(10f)
        val actual = angle330 + angle40
        assertEquals(expected, actual)
    }

    @Test
    fun `should return angle 10 when angle 20 divided by 2`() {
        val angle20 = Angle(20f)
        val expected = Angle(10f)
        val actual = angle20 / 2f
        assertEquals(expected.angle, actual.angle, 0.00001f)
    }

    @Test
    fun `should return angle 10 when angle 20 divided by -2`() {
        val angle20 = Angle(20f)
        val expected = Angle(10f)
        val actual = angle20 / -2f
        assertEquals(expected.angle, actual.angle, 0.00001f)
    }

    @Test
    fun `should be PI radius when angle is 180`() {
        val expected = Math.PI
        val actual = Angle(180f).toRadians()
        assertEquals(expected, actual, 0.00001)
    }

    @Test
    fun `should not be initialized when created with default value`() {
        val justCreated = Angle()
        assertFalse(justCreated.isInitialized())
    }

    @Test
    fun `should be initialized when created with a value`() {
        val initialized = Angle(51f)
        assertTrue(initialized.isInitialized())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw when subtracting and left arg is not initialized`() {
        val left = Angle(Float.NaN)
        val right = Angle(45f)
        left - right
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw when subtracting and right arg is not initialized`() {
        val left = Angle(45f)
        val right = Angle(Float.NaN)
        left - right
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw when adding and left arg is not initialized`() {
        val left = Angle(Float.NaN)
        val right = Angle(45f)
        left + right
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw when adding and right arg is not initialized`() {
        val left = Angle(45f)
        val right = Angle(Float.NaN)
        left + right
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw when dividing and right arg is not initialized`() {
        val left = Angle(Float.NaN)
        val right = Float.NaN
        left / right
    }

    @Test
    fun `should correctly compare angles when compared`() {
        var first = Angle(20f)
        var second = Angle(290f)
        assertTrue(first < second)

        first = Angle(310f)
        second = Angle(5f)
        assertTrue(first > second)

        first = Angle(100f)
        second = Angle(100f)
        assertTrue(first == second)
    }

    @Test
    fun `should NOT_INITIALIZED angle be not initialized`() {
        assertTrue(Angle.NOT_INITIALIZED.isInitialized().not())
    }
}