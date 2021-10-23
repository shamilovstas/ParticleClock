package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.graphics.Paint

class Bubble(
    val point: PolarPoint,
    val style: Style,
    val radius: Float
) {

    private val __preallocatedCartesian = CartesianPoint()

    fun draw(canvas: Canvas, paint: Paint, center: CartesianPoint) {
        paint.style = when (style) {
            Style.FILL -> Paint.Style.FILL
            Style.STROKE -> Paint.Style.STROKE
        }
        point.drawCircle(center, canvas, paint, radius, __preallocatedCartesian)
    }
}

enum class Style {
    FILL, STROKE
}