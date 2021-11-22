package com.shamilovstas.particleclock.model.time

import com.shamilovstas.particleclock.geometry.AnalogClockGeometry

@JvmInline
value class Hour(val value: Int) {
    init {
        require(value in AnalogClockGeometry.hoursRange)
    }
}