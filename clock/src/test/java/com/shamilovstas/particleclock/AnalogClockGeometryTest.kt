package com.shamilovstas.particleclock

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalogClockGeometryTest {

    private val analogClock = AnalogClockGeometry()

    @Test
    fun `should return 270 degrees when set 0 minutes`() {
        val expected = 270
        val actual = analogClock.minuteToAngle(Minute(0))
        assertEquals(expected, actual)
    }

    @Test
    fun `should return 0 when set 15 minutes`() {
        val expected = 0
        val actual = analogClock.minuteToAngle(Minute(15))
        assertEquals(expected, actual)
    }

    @Test
    fun `should return 90 when set 30 minutes`() {
        val expected = 90
        val actual = analogClock.minuteToAngle(Minute(30))
        assertEquals(expected, actual)
    }

    @Test
    fun `should return 180 when set 45 minutes`() {
        val expected = 180
        val actual = analogClock.minuteToAngle(Minute(45))
        assertEquals(expected, actual)
    }

    @Test
    fun `should return 270 degrees when set 0 seconds`() {
        val expected = 270
        val actual = analogClock.secondsToAngle(Second(0))
        assertEquals(expected, actual)
    }

    @Test
    fun `should return 0 when set 15 seconds`() {
        val expected = 0
        val actual = analogClock.secondsToAngle(Second(15))
        assertEquals(expected, actual)
    }

    @Test
    fun `should return 90 when set 30 seconds`() {
        val expected = 90
        val actual = analogClock.secondsToAngle(Second(30))
        assertEquals(expected, actual)
    }

    @Test
    fun `should return 180 when set 45 seconds`() {
        val expected = 180
        val actual = analogClock.secondsToAngle(Second(45))
        assertEquals(expected, actual)
    }

    @Test
    fun `should return true when minute is at start of a sector`() {
        assertTrue(analogClock.isSectorStart(Minute(35)))
    }

    @Test
    fun `should return false when minute is not at start of a sector`() {
        assertTrue(analogClock.isSectorStart(Minute(13)))
    }
}
