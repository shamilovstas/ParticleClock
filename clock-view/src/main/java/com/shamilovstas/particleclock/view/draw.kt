package com.shamilovstas.particleclock.view

import android.graphics.Canvas
import android.graphics.Paint
import com.shamilovstas.particleclock.model.angle.Sector
import com.shamilovstas.particleclock.model.point.CartesianPoint
import com.shamilovstas.particleclock.model.point.PolarPoint
import com.shamilovstas.particleclock.model.point.Radius

fun PolarPoint.drawCircle(radius: Radius, canvas: Canvas, paint: Paint) {
    val count = canvas.save()
    canvas.rotate(this.angle.angle)
    canvas.drawCircle(this.radius.value, 0f, radius.value, paint)
    canvas.restoreToCount(count)
}

fun CartesianPoint.drawCircle(radius: Radius, canvas: Canvas, paint: Paint) {
    canvas.drawCircle(x, y, radius.value, paint)
}

fun Sector.drawSector(
    center: CartesianPoint,
    radius: Radius,
    canvas: Canvas,
    paint: Paint,
    useCenter: Boolean = false
) {
    val left = center.x - radius.value
    val right = center.x + radius.value
    val top = center.y - radius.value
    val bottom = center.y + radius.value
    canvas.drawArc(left, top, right, bottom, start.angle, end.angle, useCenter, paint)
}
