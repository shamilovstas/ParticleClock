package com.shamilovstas.particleclock

// It's assumed the clock is drawn in clockwise direction
class AnalogClockGeometry {

    companion object {
        private const val ANGLE_AT_0_SECONDS = 270
        private const val DEGREES_PER_MINUTE = 6
        private const val FULL_ANGLE = 360
        private const val MIN_MINUTE = 0
        private const val MAX_MINUTE = 59
        private const val SECTOR_LENGTH = 5

    }

    fun minuteToAngle(minute: Minute): Int {
        require(minute.value in MIN_MINUTE..MAX_MINUTE)
        val angle = (ANGLE_AT_0_SECONDS + minute.value * DEGREES_PER_MINUTE) % FULL_ANGLE
        return angle
    }

    fun secondsToAngle(second: Second): Int = minuteToAngle(Minute(second.value))

    fun isSectorStart(minute: Minute): Boolean {
        return minute.value % SECTOR_LENGTH == 0
    }
}

@JvmInline
value class Minute(val value: Int) {
    init {
        require(value in 0..59)
    }
}

@JvmInline
value class Hour(val value: Int) {
    init {
        require(value in 0..11)
    }
}

@JvmInline
value class Second(val value: Int) {
    init {
        require(value in 0..59)
    }
}