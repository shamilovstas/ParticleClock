package com.shamilov.particleclock.model

import com.shamilovstas.particleclock.model.angle.Angle
import com.shamilovstas.particleclock.model.angle.Sector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SectorTest {

    @Test(expected = IllegalArgumentException::class)
    fun `should throw when calculating range and start angle is not initialized`() {
        val start = Angle(Float.NaN)
        val end = Angle(45f)
        val sector = Sector(start, end)
        sector.asRange()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw when calculating range and end angle is not initialized`() {
        val start = Angle(45f)
        val end = Angle(Float.NaN)
        val sector = Sector(start, end)
        sector.asRange()
    }

    @Test
    fun `should return single value range when sector angles are equal`() {
        val rawAngle = 45f
        val start = Angle(rawAngle)
        val end = Angle(rawAngle)
        val sector = Sector(start, end)
        val range = sector.asRange()
        assertEquals(range.first, range.last)
    }

    @Test
    fun `should return empty range when sector start angle is greater than end`() {
        val start = Angle(310f)
        val end = Angle(20f)
        assertTrue(start > end)
        val sector = Sector(start, end)
        val range = sector.asRange()
        assertTrue(range.isEmpty())
    }
}
