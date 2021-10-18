package com.shamilovstas.particleclock

import android.graphics.Rect
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

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
        val radius = 100
        val center = Point(100, 500)
        val expected = Rect().apply {
            left = center.x - radius
            right = center.x + radius
            top = center.y - radius
            bottom = center.y + radius
        }
        circle.center = center
        circle.radius = radius

        assertEquals(expected, circle.boundingRect)
    }
}