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

    // 5 - 10 = 355
}