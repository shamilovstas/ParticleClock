package com.shamilovstas.particleclock

import androidx.annotation.FloatRange
import kotlin.math.roundToInt

data class Sector(
    var start: Angle = Angle.NOT_INITIALIZED,
    var end: Angle = Angle.NOT_INITIALIZED
) {
    fun asRange(): IntRange {
        return start.angle.roundToInt()..end.angle.roundToInt()
    }
}