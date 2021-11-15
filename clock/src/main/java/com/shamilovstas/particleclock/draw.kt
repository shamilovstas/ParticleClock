package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.graphics.Paint

fun Circle.init(canvas: Canvas, paint: Paint) {
    canvas.drawCircle(center.x, center.y, radius.value, paint)
}
