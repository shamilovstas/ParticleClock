package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.graphics.Paint

fun Circle.draw(canvas: Canvas, paint: Paint) {
    canvas.drawCircle(center.x, center.y, radius, paint)
}

fun PolarCoordinate.drawCircle(
    center: CartesianPoint,
    canvas: Canvas,
    paint: Paint,
    radius: Float,
    holder: CartesianPoint = CartesianPoint()
) {
    this.toCartesian(holder)
    canvas.drawCircle(holder.x + center.x, holder.y + center.y, radius, paint)
}