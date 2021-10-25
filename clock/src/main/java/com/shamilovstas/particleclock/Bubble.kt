package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.graphics.Paint

class Bubble(
    val point: PolarPoint,
    val style: Style,
    var radius: Float,
    val autoMove: Boolean
) {

    private val __preallocatedCartesian = CartesianPoint()

    fun draw(canvas: Canvas, paint: Paint) {
        paint.style = when (style) {
            Style.FILL -> Paint.Style.FILL
            Style.STROKE -> Paint.Style.STROKE
        }
        point.drawCircle(canvas, paint, radius, __preallocatedCartesian)
    }
}

enum class Style {
    FILL, STROKE
}