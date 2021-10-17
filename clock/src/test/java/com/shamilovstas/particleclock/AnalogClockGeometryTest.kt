package com.shamilovstas.particleclock

import org.junit.Assert.assertEquals
import org.junit.Test

class AnalogClockGeometryTest {

    private val point = Point(0, 0)
    private val radius = 100
    private val analogClock = AnalogClockGeometry(Circle(point, radius))


    @Test
    fun `should return 270 degrees when set 0 minutes`() {
        val expected = 270
        val actual = analogClock.minuteToAngle(0)
        assertEquals(expected, actual)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when minute is less than 0`() {
        analogClock.minuteToAngle(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when minute is more than 59`() {
        analogClock.minuteToAngle(60)
    }

    @Test
    fun `should return 0 when set 15 minutes`() {
        val expected = 0
        val actual = analogClock.minuteToAngle(15)
        assertEquals(expected, actual)
    }

    @Test
    fun `should return 90 when set 30 minutes`() {
        val expected = 90
        val actual = analogClock.minuteToAngle(30)
        assertEquals(expected, actual)
    }

    @Test
    fun `should return 180 when set 45 minutes`() {
        val expected = 180
        val actual = analogClock.minuteToAngle(45)
        assertEquals(expected, actual)
    }

    @Test
    fun `should return 270 degrees when set 0 seconds`() {
        val expected = 270
        val actual = analogClock.secondsToAngle(0)
        assertEquals(expected, actual)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when second is less than 0`() {
        analogClock.secondsToAngle(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when second is more than 59`() {
        analogClock.secondsToAngle(60)
    }

    @Test
    fun `should return 0 when set 15 seconds`() {
        val expected = 0
        val actual = analogClock.secondsToAngle(15)
        assertEquals(expected, actual)
    }

    @Test
    fun `should return 90 when set 30 seconds`() {
        val expected = 90
        val actual = analogClock.secondsToAngle(30)
        assertEquals(expected, actual)
    }

    @Test
    fun `should return 180 when set 45 seconds`() {
        val expected = 180
        val actual = analogClock.secondsToAngle(45)
        assertEquals(expected, actual)
    }
}
