package com.shamilov.particleclock.model

import com.shamilovstas.particleclock.model.time.Minute
import org.junit.Assert.assertEquals
import org.junit.Test

class MinuteTest {

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when minute is less than 0`() {
        Minute(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception when minute is more than 59`() {
        Minute(60)
    }

    @Test
    fun `should properly create minute when constructed`() {
        val expected = 24
        val actual = Minute(24).value
        assertEquals(expected, actual)
    }
}