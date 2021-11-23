package com.shamilovstas.particleclock.hand

import android.graphics.Canvas
import com.shamilovstas.particleclock.model.angle.Angle
import com.shamilovstas.particleclock.model.point.Radius
import com.shamilovstas.particleclock.view.hand.Hand
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

class HandTest {

    @Test
    fun `should calculate hand sector when angle is set`() {
        val sweepAngle = com.shamilovstas.particleclock.model.angle.Angle(10.toFloat())
        val handAngle = com.shamilovstas.particleclock.model.angle.Angle(120f)
        val hand = Hand(Radius(12f), sweepAngle)
        hand.angle = handAngle

        val expectedStart = handAngle - sweepAngle / 2f
        val expectedEnd = handAngle  + sweepAngle / 2f
        assertEquals(expectedStart, hand.sector.start)
        assertEquals(expectedEnd, hand.sector.end)
    }

    @Test
    fun `should not draw when angle is not set`() {
        val hand = Hand(Radius(12f))
        val canvas = mock<Canvas>()

        hand.draw(canvas, mock())
        verifyNoInteractions(canvas)
    }

    @Test
    fun `should draw when angle is set`() {
        val hand = Hand(Radius(12f))
        hand.angle = com.shamilovstas.particleclock.model.angle.Angle(230f)
        val canvas = mock<Canvas>()

        hand.draw(canvas, mock())
        verify(canvas)
    }
}
