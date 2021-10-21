package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.graphics.Paint

class Bubble(
    val circle: Circle = Circle(),
    val style: Style
)

fun Bubble.draw(canvas: Canvas, paint: Paint) {
    paint.style = when (style) {
        Style.FILL -> Paint.Style.FILL
        Style.STROKE -> Paint.Style.STROKE
    }
    circle.draw(canvas, paint)
}

enum class Style {
    FILL, STROKE
}