package com.shamilovstas.particleclock

import org.junit.Assert.assertEquals
import org.junit.Test

class CircleTest {

    private val circle = Circle()

    @Test
    fun `should set circle center coordinates when circle coordinates are set`() {

        val expectedX = 52
        val expectedY = 82
        circle.x = expectedX
        circle.y = expectedY
        assertEquals(circle.x, expectedX)
        assertEquals(circle.y, expectedY)
    }

    @Test
    fun `should set fields to 0 when refresh is called`() {
        circle.radius = com.shamilovstas.particleclock.model.point.Radius(25f)
        circle.center.x = 52f
        circle.center.y = 512f

        circle.refresh()
        assertEquals(circle.x, 0)
        assertEquals(circle.y, 0)
        assertEquals(circle.radius.value, 0f)
    }
}