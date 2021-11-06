package com.shamilovstas.particleclock.point

import com.shamilovstas.particleclock.Angle
import com.shamilovstas.particleclock.CartesianCoordinate
import com.shamilovstas.particleclock.CartesianPoint
import com.shamilovstas.particleclock.PolarPoint
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test

class CartesianPointTest {
    private val angleDelta = 0.000001f

    @Test
    fun `should clean cartesian point when refresh is called`() {
        val point = CartesianPoint(-5f, 0f)
        point.refresh()
        assertEquals(point.x, 0f, angleDelta)
        assertEquals(point.y, 0f, angleDelta)
    }

    @Test
    fun `should convert polar point when converted from cartesian`() {
        val expected = PolarPoint(5f)
        expected.angle = Angle(180f)

        val cartesian: CartesianCoordinate = CartesianPoint(-5f, 0f)
        val actual = cartesian.toPolar()

        assertEquals(expected.angle.angle, actual.angle.angle, angleDelta)
        assertEquals(expected.radius, actual.radius, angleDelta)
    }

    @Test
    fun `should convert cartesian point when used existing instance`() {
        val expected = CartesianPoint(-5f, 0f)
        val polar = PolarPoint(5f)
        polar.angle = Angle(180f)

        val actual = CartesianPoint()
        polar.toCartesian(actual)

        assertEquals(expected.x, actual.x, angleDelta)
        assertEquals(expected.y, actual.y, angleDelta)
    }

    @Test
    fun `should init cartesian when copied from another instance`() {
        val expected = CartesianPoint(100, 200)
        val actual = CartesianPoint()
        actual.copyFrom(expected)
        assertEquals(expected, actual)
    }

    @Test
    fun `should have equal hashcodes when cartesian points are equal`() {
        val x = 50
        val y = 24
        val first = CartesianPoint(x, y)
        val second = CartesianPoint(x, y)
        assertEquals(first.hashCode(), second.hashCode())
    }

    @Test
    fun `should return true when cartesian points are equal`() {
        val x = 50
        val y = 24
        val first = CartesianPoint(x, y)
        val second = CartesianPoint(x, y)
        assertTrue(first == second)
    }

    @Test
    fun `should return false when cartesian points are not equal`() {
        val first = CartesianPoint(100, 200)
        val second = CartesianPoint(50, 25)
        assertFalse(first == second)
    }
}
