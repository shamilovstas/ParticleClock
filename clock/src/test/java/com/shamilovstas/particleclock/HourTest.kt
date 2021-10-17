package com.shamilovstas.particleclock

import org.junit.Assert
import org.junit.Test

class HourTest {

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when hour is less than 0`() {
        Hour(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when hour is more than 11`() {
        Hour(12)
    }

    @Test
    fun `should properly create hour when constructed`() {
        val expected = 4
        val actual = Hour(4).value
        Assert.assertEquals(expected, actual)
    }
}