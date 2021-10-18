package com.shamilovstas.particleclock

import org.junit.Assert.assertEquals
import org.junit.Test

class PointTest {

    private val point = Point()

    @Test
    fun `should clean fields when refresh is called`() {
        point.x = 22
        point.y = 33
        point.refresh()
        assertEquals(point.x, 0)
        assertEquals(point.y, 0)
    }
}