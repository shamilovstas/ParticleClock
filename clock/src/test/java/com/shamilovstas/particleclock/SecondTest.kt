package com.shamilovstas.particleclock

import org.junit.Assert
import org.junit.Test

class SecondTest {

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when second is less than 0`() {
        Second(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when second is more than 59`() {
        Second(60)
    }

    @Test
    fun `should properly create second when constructed`() {
        val expected = 24
        val actual = Second(24).value
        Assert.assertEquals(expected, actual)
    }
}