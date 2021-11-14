package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.sin

class Particle(
    val coordinateCenter: CartesianPoint = CartesianPoint(),
    val point: PolarPoint,
    val style: Style,
    var radius: Radius
) {

    val initialRadius = radius
    var alpha: Int = 255

    fun draw(canvas: Canvas, paint: Paint) {
        paint.style = when (style) {
            Style.FILL -> Paint.Style.FILL
            Style.STROKE -> Paint.Style.STROKE
        }
        paint.alpha = alpha
        val count = canvas.save()
        canvas.rotate(point.angle.angle)
        canvas.drawCircle(point.radius.value, coordinateCenter.y, radius.value, paint)
        canvas.restoreToCount(count)
    }
}

enum class Style {
    FILL, STROKE
}
