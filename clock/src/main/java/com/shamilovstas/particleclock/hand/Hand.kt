package com.shamilovstas.particleclock.hand

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.shamilovstas.particleclock.*
import java.util.*
import kotlin.random.Random


class Hand(
    radius: Radius = Radius(0f),
    var sweepAngle: Angle = Angle(10f)
) {
    var radius: Radius = radius
        set(value) {
            field = value
            generateBubbles(field)
        }

    // region preallocated
    private var polarPoint = PolarPoint()
    private var cartesianPoint = CartesianPoint()

    private val paint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 4f
    }

    // endregion
    private val bubbles = mutableListOf<Bubble>()

    val sector = Sector()
    var angle = Angle()
        set(value) {
            field = value
            val sweepHalf = sweepAngle / 2f
            sector.start = value - sweepHalf
            sector.end = value + sweepAngle - sweepHalf
        }

    init {
        generateBubbles(radius)
    }

    fun draw(canvas: Canvas, paint: Paint) {
        if (angle.isInitialized()) {
            polarPoint.angle = angle
            polarPoint.radius = radius
            polarPoint.toCartesian(cartesianPoint)

            Log.d("Hand", bubbles.joinToString { it.toString() })
            bubbles.forEach { it.draw(canvas, this.paint) }
        }
    }

    private fun generateBubbles(maxRadius: Radius) {
        Log.d("Hand", "generateBubbles")
        bubbles.clear()

        val bubbleRadius = Radius(10f)

        var previous: Bubble? = null
        while (previous == null || hasRoomLeft(previous, maxRadius)) {
            val angleVariation = Random.nextFloat(-0.5f, 0.5f)
            val angle = Angle(angle.angle + angleVariation)

            val startRadius = previous?.radius ?: Radius(0f)
            val endRadius = Radius(startRadius.value + 2 * bubbleRadius.value)

            val radius = Random.nextFloat(startRadius.value, endRadius.value)
            val point = PolarPoint(Radius(radius), angle)
            val bubble = Bubble(point, Style.FILL, bubbleRadius, true)
            previous = bubble
            bubbles.add(bubble)
        }
    }

    private fun hasRoomLeft(bubble: Bubble, distance: Radius): Boolean {
        return distance - bubble.point.radius > bubble.radius
    }
}