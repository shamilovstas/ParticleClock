package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.sin
import kotlin.random.Random

class Bubble(
    val point: PolarPoint,
    val style: Style,
    var radius: Radius,
    val autoMove: Boolean
) {

    private var alpha: Int = 1

    companion object {
        const val INITIAL_RADIUS = 10f
    }

    val cartesianPoint: CartesianPoint = point.toCartesian()

    fun draw(canvas: Canvas, paint: Paint) {
        paint.style = when (style) {
            Style.FILL -> Paint.Style.FILL
            Style.STROKE -> Paint.Style.STROKE
        }
        paint.alpha = alpha
        point.drawCircle(canvas, paint, radius, cartesianPoint)
    }

    fun setRadiusMultiplier(multiplier: Double) {
        this.alpha = (sin(multiplier * Math.PI) * 255f).toInt()
        this.radius = Radius(INITIAL_RADIUS * sin(multiplier * Math.PI).toFloat())
    }
}

enum class Style {
    FILL, STROKE
}
