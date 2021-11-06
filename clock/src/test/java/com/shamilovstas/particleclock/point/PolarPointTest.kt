package com.shamilovstas.particleclock.point

import com.shamilovstas.particleclock.Angle
import com.shamilovstas.particleclock.CartesianPoint
import com.shamilovstas.particleclock.PolarPoint
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test

class PolarPointTest {

    @Test
    fun `should clean polar point when refresh is called`() {
        val point = PolarPoint(180f)
        point.angle = Angle(5f)
        point.refresh()
        assertEquals(point.radius, 0f, Angle.ANGLE_DELTA)
        assertEquals(point.angle.angle, 0f, Angle.ANGLE_DELTA)
    }

    @Test
    fun `should convert cartesian point when converted from polar`() {
        val expected = CartesianPoint(-5f, 0f)
        val polar = PolarPoint(5f)
        polar.angle = Angle(180f)

        val actual = polar.toCartesian()

        assertEquals(expected.x, actual.x, Angle.ANGLE_DELTA)
        assertEquals(expected.y, actual.y, Angle.ANGLE_DELTA)
    }

    @Test
    fun `should convert polar point when used existing instance`() {
        val expected = PolarPoint(5f)
        expected.angle = Angle(180f)

        val actual = PolarPoint()
        val cartesian = CartesianPoint(-5f, 0f)
        cartesian.toPolar(actual)

        assertEquals(expected.angle.angle, actual.angle.angle, Angle.ANGLE_DELTA)
        assertEquals(expected.radius, actual.radius, Angle.ANGLE_DELTA)
    }

    @Test
    fun `should init polar when copied from another instance`() {
        val expected = PolarPoint(180f)
        expected.angle = Angle(5f)

        val actual = PolarPoint()
        actual.copyFrom(expected)
        assertEquals(expected, actual)
    }

    @Test
    fun `should have equal hashcodes when polar points are equal`() {
        val radius = 20f
        val angle = Angle(45f)
        val first = PolarPoint(radius).apply { this.angle = angle }
        val second = PolarPoint(radius).apply { this.angle = angle }
        assertEquals(first.hashCode(), second.hashCode())
    }

    @Test
    fun `should return true when cartesian points are equal`() {
        val radius = 20f
        val angle = Angle(45f)
        val first = PolarPoint(radius).apply { this.angle = angle }
        val second = PolarPoint(radius).apply { this.angle = angle }
        assertTrue(first == second)
    }

    @Test
    fun `should return false when cartesian points are not equal`() {
        val first = PolarPoint(23f).apply { this.angle = Angle(21f) }
        val second = PolarPoint(89f).apply { this.angle = Angle(87f) }
        assertFalse(first == second)
    }
}
