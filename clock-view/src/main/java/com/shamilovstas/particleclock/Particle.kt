package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.graphics.withRotation
import com.shamilovstas.particleclock.model.point.CartesianPoint
import com.shamilovstas.particleclock.model.point.PolarPoint

class Particle(
    val coordinateCenter: CartesianPoint = CartesianPoint(),
    val point: PolarPoint,
    val style: Style,
    var radius: com.shamilovstas.particleclock.model.point.Radius
) {
    val initialRadius = radius
    var alpha: Int = 255

    fun draw(canvas: Canvas, paint: Paint) {
        paint.style = when (style) {
            Style.FILL -> Paint.Style.FILL
            Style.STROKE -> Paint.Style.STROKE
        }
        paint.alpha = alpha
        canvas.withRotation(point.angle.angle) {
            canvas.drawCircle(point.radius.value, coordinateCenter.y, radius.value, paint)
        }
    }
}

enum class Style {
    FILL, STROKE
}
