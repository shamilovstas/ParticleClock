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

    private var alpha: Int = 255

    companion object {
        const val INITIAL_RADIUS = 10f
    }

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

    fun setRadiusMultiplier(multiplier: Double) {
        this.alpha = (sin(multiplier * Math.PI) * 255f).toInt()
        this.radius = Radius(INITIAL_RADIUS * sin(multiplier * Math.PI).toFloat())
    }
}

enum class Style {
    FILL, STROKE
}
