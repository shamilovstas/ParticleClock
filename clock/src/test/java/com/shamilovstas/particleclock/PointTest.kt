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

    @Test
    fun `should convert cartesian point when converted from polar`() {
        val expected = CartesianPoint(-5, 0)
        val polar = PolarPoint(180f, 5)
        val actual = polar.toCartesian()

        assertEquals(expected, actual)
    }

    @Test
    fun `should convert polar point when converted from cartesian`() {
        val expected = PolarPoint(180f, 5)
        val cartesian: CartesianCoordinate = CartesianPoint(-5, 0)
        val actual = cartesian.toPolar()

        assertEquals(expected, actual)
    }
}