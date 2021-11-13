package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.sin
import kotlin.random.Random

class Particle(
    val coordinateCenter: CartesianPoint = CartesianPoint(),
    val point: PolarPoint,
    val style: Style,
    var radius: Radius
) {

    private val initialRadius = radius
    private var alpha: Int = 255

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

    fun setDistanceMultiplier(multiplier: Double) {
        val sin = sin(multiplier * Math.PI)
        this.alpha = (sin * 255f).toInt()
        this.radius = Radius(initialRadius.value * sin.toFloat())
    }
}

enum class Style {
    FILL, STROKE
}
