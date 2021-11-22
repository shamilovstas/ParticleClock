package com.shamilovstas.particleclock

import android.graphics.RectF
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class CircleInstrumentedTest {
    private val circle = Circle()

    @Test
    fun shouldReturnCorrectBoundingRectWhenCenterAndRadiusAreSet() {
        val radius = 100f
        val center = CartesianPoint(100, 500)
        val expected = RectF().apply {
            left = center.x - radius
            right = center.x + radius
            top = center.y - radius
            bottom = center.y + radius
        }
        circle.center.copyFrom(center)
        circle.radius = com.shamilovstas.particleclock.model.point.Radius(radius)

        assertEquals(expected, circle.boundingRectF)
    }
}