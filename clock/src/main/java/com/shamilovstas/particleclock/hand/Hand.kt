package com.shamilovstas.particleclock.hand

import android.graphics.Canvas
import com.shamilovstas.particleclock.Angle
import com.shamilovstas.particleclock.Sector

class Hand {

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

    }
}