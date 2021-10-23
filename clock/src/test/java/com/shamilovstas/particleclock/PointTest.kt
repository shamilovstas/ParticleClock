package com.shamilovstas.particleclock

import org.junit.Assert.assertEquals
import org.junit.Test

class PointTest {

    private val angleDelta = 0.000001f

    @Test
    fun `should clean cartesian point when refresh is called`() {
        val point = CartesianPoint(-5f, 0f)
        point.refresh()
        assertEquals(point.x, 0f, angleDelta)
        assertEquals(point.y, 0f, angleDelta)
    }

    @Test
    fun `should clean polar point when refresh is called`() {
        val point = PolarPoint(180f, 5f)
        point.refresh()
        assertEquals(point.radius, 0f, angleDelta)
        assertEquals(point.angle, 0f, angleDelta)
    }

    @Test
    fun `should convert cartesian point when converted from polar`() {
        val expected = CartesianPoint(-5f, 0f)
        val polar = PolarPoint(180f, 5f)
        val actual = polar.toCartesian()

        assertEquals(expected.x, actual.x, angleDelta)
        assertEquals(expected.y, actual.y, angleDelta)
    }

    @Test
    fun `should convert polar point when converted from cartesian`() {
        val expected = PolarPoint(180f, 5f)
        val cartesian: CartesianCoordinate = CartesianPoint(-5f, 0f)
        val actual = cartesian.toPolar()

        assertEquals(expected.angle, actual.angle, angleDelta)
        assertEquals(expected.radius, actual.radius, angleDelta)
    }

    @Test
    fun `should convert polar point when used existing instance`() {
        val expected = PolarPoint(180f, 5f)
        val actual = PolarPoint()
        val cartesian = CartesianPoint(-5f, 0f)
        cartesian.toPolar(actual)

        assertEquals(expected.angle, actual.angle, angleDelta)
        assertEquals(expected.radius, actual.radius, angleDelta)
    }

    @Test
    fun `should convert cartesian point when used existing instance`() {
        val expected = CartesianPoint(-5f, 0f)
        val polar = PolarPoint(180f, 5f)
        val actual = CartesianPoint()
        polar.toCartesian(actual)

        assertEquals(expected.x, actual.x, angleDelta)
        assertEquals(expected.y, actual.y, angleDelta)
    }
}