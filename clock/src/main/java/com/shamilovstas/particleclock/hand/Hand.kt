package com.shamilovstas.particleclock.hand

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.shamilovstas.particleclock.Angle
import com.shamilovstas.particleclock.CartesianPoint
import com.shamilovstas.particleclock.PolarPoint
import com.shamilovstas.particleclock.Sector

class Hand(var radius: Float = 0f) {

    // region preallocated
    private var polarPoint = PolarPoint(radius = radius)
    private var cartesianPoint = CartesianPoint()
    // endregion

    val minutePaint = Paint().apply {
        this.color = Color.BLUE
        this.strokeWidth = 4.0f
        this.style = Paint.Style.STROKE
    }

    val sector = Sector()
    var angle = Angle()
        set(value) {
            field = value
            val sweepHalf = sectorSweepAngle / 2f
            sector.start = value - sweepHalf
            sector.end = value + sectorSweepAngle - sweepHalf
        }

    companion object {
        private val sectorSweepAngle = Angle(10f)
    }

    fun draw(canvas: Canvas) {
        if (angle.isInitialized()) {
            polarPoint.angle = angle
            polarPoint.radius = radius
            polarPoint.toCartesian(cartesianPoint)

            canvas.drawLine(
                0f, 0f,
                cartesianPoint.x,
                cartesianPoint.y,
                minutePaint
            )
        }
    }
}