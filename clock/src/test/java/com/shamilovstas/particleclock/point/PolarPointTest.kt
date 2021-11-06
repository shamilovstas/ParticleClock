package com.shamilovstas.particleclock.point

import com.shamilovstas.particleclock.Angle
import com.shamilovstas.particleclock.CartesianPoint
import com.shamilovstas.particleclock.PolarPoint
import org.junit.Assert
import org.junit.Test

class PolarPointTest {

    private val angleDelta = 0.000001f

    @Test
    fun `should clean polar point when refresh is called`() {
        val point = PolarPoint(180f)
        point.angle = Angle(5f)
        point.refresh()
        Assert.assertEquals(point.radius, 0f, angleDelta)
        Assert.assertEquals(point.angle.angle, 0f, angleDelta)
    }

    @Test
    fun `should convert cartesian point when converted from polar`() {
        val expected = CartesianPoint(-5f, 0f)
        val polar = PolarPoint(5f)
        polar.angle = Angle(180f)

        val actual = polar.toCartesian()

        Assert.assertEquals(expected.x, actual.x, angleDelta)
        Assert.assertEquals(expected.y, actual.y, angleDelta)
    }

    @Test
    fun `should convert polar point when used existing instance`() {
        val expected = PolarPoint(5f)
        expected.angle = Angle(180f)

        val actual = PolarPoint()
        val cartesian = CartesianPoint(-5f, 0f)
        cartesian.toPolar(actual)

        Assert.assertEquals(expected.angle.angle, actual.angle.angle, angleDelta)
        Assert.assertEquals(expected.radius, actual.radius, angleDelta)
    }

    @Test
    fun `should init polar when copied from another instance`() {
        val expected = PolarPoint(180f)
        expected.angle = Angle(5f)

        val actual = PolarPoint()
        actual.copyFrom(expected)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should have equal hashcodes when polar points are equal`() {
        val radius = 20f
        val angle = Angle(45f)
        val first = PolarPoint(radius).apply { this.angle = angle }
        val second = PolarPoint(radius).apply { this.angle = angle }
        Assert.assertEquals(first.hashCode(), second.hashCode())
    }

    @Test
    fun `should return true when cartesian points are equal`() {
        val radius = 20f
        val angle = Angle(45f)
        val first = PolarPoint(radius).apply { this.angle = angle }
        val second = PolarPoint(radius).apply { this.angle = angle }
        Assert.assertTrue(first == second)
    }

    @Test
    fun `should return false when cartesian points are not equal`() {
        val first = PolarPoint(23f).apply { this.angle = Angle(21f) }
        val second = PolarPoint(89f).apply { this.angle = Angle(87f) }
        Assert.assertFalse(first == second)
    }
}
