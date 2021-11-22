package com.shamilovstas.particleclock.model.time

import com.shamilovstas.particleclock.geometry.AnalogClockGeometry

@JvmInline
value class Minute(val value: Int) {
    init {
        require(value in AnalogClockGeometry.minutesRange)
    }
}