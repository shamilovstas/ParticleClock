package com.shamilovstas.particleclock

import kotlin.math.roundToInt

data class Sector(
    var start: Angle = Angle.NOT_INITIALIZED,
    var end: Angle = Angle.NOT_INITIALIZED
) {
    fun asRange(): IntRange {
        require(start.isInitialized())
        require(end.isInitialized())
        return start.angle.roundToInt()..end.angle.roundToInt()
    }
}