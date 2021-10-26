package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.graphics.Paint

class Bubble(
    val point: PolarPoint,
    val style: Style,
    var radius: Float,
    val autoMove: Boolean
) {

    companion object {
        const val INITIAL_RADIUS = 10f
        const val MULTIPLIER_ADJUSTMENT_FACTOR = 0.5f
    }

    private val __preallocatedCartesian = CartesianPoint()

    fun draw(canvas: Canvas, paint: Paint) {
        paint.style = when (style) {
            Style.FILL -> Paint.Style.FILL
            Style.STROKE -> Paint.Style.STROKE
        }
        point.drawCircle(canvas, paint, radius, __preallocatedCartesian)
    }

    fun setRadiusMultiplier(multiplier: Double) {
        this.radius = INITIAL_RADIUS * (multiplier.toFloat() + MULTIPLIER_ADJUSTMENT_FACTOR)
    }
}

enum class Style {
    FILL, STROKE
}