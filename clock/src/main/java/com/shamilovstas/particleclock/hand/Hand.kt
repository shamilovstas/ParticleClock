package com.shamilovstas.particleclock.hand

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.shamilovstas.particleclock.Angle
import com.shamilovstas.particleclock.CartesianPoint
import com.shamilovstas.particleclock.PolarPoint
import com.shamilovstas.particleclock.Sector


// TODO tests
class Hand(
    var radius: Float = 0f,
    var sweepAngle: Angle = Angle(10f)
) {

    // region preallocated
    private var polarPoint = PolarPoint(radius = radius)
    private var cartesianPoint = CartesianPoint()
    // endregion

    val sector = Sector()
    var angle = Angle()
        set(value) {
            field = value
            val sweepHalf = sweepAngle / 2f
            sector.start = value - sweepHalf
            sector.end = value + sweepAngle - sweepHalf
        }

    fun draw(canvas: Canvas, paint: Paint) {
        if (angle.isInitialized()) {
            polarPoint.angle = angle
            polarPoint.radius = radius
            polarPoint.toCartesian(cartesianPoint)

            canvas.drawLine(
                0f, 0f,
                cartesianPoint.x,
                cartesianPoint.y,
                paint
            )
        }
    }
}