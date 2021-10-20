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
        assertEquals(circle.center.x, expectedX)
        assertEquals(circle.center.y, expectedY)
    }

    @Test
    fun `should return rightmost point when requested at 0 degrees`() {
        val radius = 100
        val center = Point(100, 100)
        circle.center.copyFrom(center)
        circle.radius = radius
        val expected = Point(radius + center.x, center.y)
        val actual = Point().apply {
            circle.getPoint(0f, this)
        }
        assertEquals(expected, actual)
    }

    @Test
    fun `should set fields to 0 when refresh is called`() {
        circle.radius = 25
        circle.center.x = 52
        circle.center.y = 512

        circle.refresh()
        assertEquals(circle.center.x, 0)
        assertEquals(circle.center.y, 0)
        assertEquals(circle.radius, 0)
    }

    @Test
    fun `should set circle center when copied from another instance`() {
        val expected = Point(100, 200)
        val actual = Point()
        actual.copyFrom(expected)
        assertEquals(expected, actual)
    }
}