package com.shamilov.particleclock.model

import com.shamilovstas.particleclock.model.time.Second
import org.junit.Assert
import org.junit.Assert.assertEquals
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
        assertEquals(expected, actual)
    }
}