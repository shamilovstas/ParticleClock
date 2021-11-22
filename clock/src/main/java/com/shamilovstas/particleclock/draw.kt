package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.graphics.withRotation
import com.shamilovstas.particleclock.model.point.PolarPoint
import com.shamilovstas.particleclock.model.point.Radius

fun Circle.draw(canvas: Canvas, paint: Paint) {
    canvas.drawCircle(center.x, center.y, radius.value, paint)
}

fun PolarPoint.drawCircle(radius: Radius, canvas: Canvas, paint: Paint) {
    canvas.withRotation(this.angle.angle) {
        canvas.drawCircle(
            this@drawCircle.radius.value,
            0f,
            radius.value,
            paint
        )
    }
}
