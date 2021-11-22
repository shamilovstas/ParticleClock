package com.shamilov.particleclock.model

import com.shamilovstas.particleclock.model.point.Radius
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RadiusTest {

    @Test(expected = IllegalArgumentException::class)
    fun `should throw when radius is negative`() {
        Radius(-25f)
    }

    @Test
    fun `should return radius 630 when raw 50 subtracted from radius 680`() {
        val radius = Radius(680f)
        val operand = 50f
        val expected = Radius(630f)
        val actual = radius - operand
        assertEquals(expected, actual)
    }

    @Test
    fun `should return radius 630 when radius 50 subtracted from radius 680`() {
        val radius = Radius(680f)
        val operand = Radius(50f)
        val expected = Radius(630f)
        val actual = radius - operand
        assertEquals(expected, actual)
    }

    @Test
    fun `should return radius 0 when subtraction result should be negative`() {
        val radius = Radius(20f)
        val operand = Radius(50f)
        val expected = Radius(0f)
        val actual = radius - operand
        assertEquals(expected, actual)
    }

    @Test
    fun `should return radius 630 when raw 50 added to radius 580`() {
        val radius = Radius(580f)
        val operand = 50f
        val expected = Radius(630f)
        val actual = radius + operand
        assertEquals(expected, actual)
    }

    @Test
    fun `should return radius 630 when radius 50 added to radius 580`() {
        val radius = Radius(580f)
        val operand = Radius(50f)
        val expected = Radius(630f)
        val actual = radius + operand
        assertEquals(expected, actual)
    }

    @Test
    fun `should return radius 250 when radius 500 divided by radius 2`() {
        val radius = Radius(500f)
        val operand = Radius(2f)
        val expected = Radius(250f)
        val actual = radius / operand
        assertEquals(expected, actual)
    }

    @Test
    fun `should return radius 250 when radius 500 divided by raw 2`() {
        val radius = Radius(500f)
        val operand = 2f
        val expected = Radius(250f)
        val actual = radius / operand
        assertEquals(expected, actual)
    }

    @Test
    fun `should return true when first radius is greater than second radius`() {
        assertTrue(
            Radius(500f) > Radius(
                250f
            )
        )
    }

    @Test
    fun `should return true when first radius is smaller than second radius`() {
        assertTrue(Radius(250f) < Radius(500f))
    }

    @Test
    fun `should return true when first radius is equal to second radius`() {
        assertTrue(Radius(500f) == Radius(500f)
        )
    }
}