package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.graphics.Paint

fun Circle.draw(canvas: Canvas, paint: Paint) {
    canvas.drawCircle(center.x, center.y, radius.value, paint)
}

fun PolarCoordinate.drawCircle(
    canvas: Canvas,
    paint: Paint,
    radius: Radius,
    holder: CartesianPoint = CartesianPoint()
) {
    this.toCartesian(holder)
    canvas.drawCircle(holder.x, holder.y, radius.value, paint)
}