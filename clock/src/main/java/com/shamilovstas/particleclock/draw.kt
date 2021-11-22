package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.graphics.Paint
import com.shamilovstas.particleclock.model.point.PolarPoint
import com.shamilovstas.particleclock.model.point.Radius

fun Circle.draw(canvas: Canvas, paint: Paint) {
    canvas.drawCircle(center.x, center.y, radius.value, paint)
}

fun PolarPoint.drawCircle(radius: Radius, canvas: Canvas, paint: Paint) {
    val count = canvas.save()
    canvas.rotate(this.angle.angle)
    canvas.drawCircle(this.radius.value, 0f, radius.value, paint)
    canvas.restoreToCount(count)
}
