package com.shamilovstas.particleclock

// It's assumed the clock is drawn in clockwise direction
class AnalogClockGeometry(val circle: Circle) {

    companion object {
        private const val ANGLE_AT_0_SECONDS = 270
        private const val DEGREES_PER_MINUTE = 6
        private const val FULL_ANGLE = 360
        private const val MIN_MINUTE = 0
        private const val MAX_MINUTE = 59

    }

    fun minuteToAngle(minute: Int): Int {
        require(minute in MIN_MINUTE..MAX_MINUTE)
        val angle = (ANGLE_AT_0_SECONDS + minute * DEGREES_PER_MINUTE) % FULL_ANGLE
        return angle
    }

    fun secondsToAngle(second: Int): Int = minuteToAngle(second)
}