package com.shamilovstas.particleclock

// It's assumed the clock is drawn in clockwise direction
class AnalogClockGeometry {

    companion object {
        private const val CLOCK_START_ANGLE = 270
        private const val DEGREES_PER_MINUTE = 6
        private const val DEGREES_PER_SECOND = 6
        private const val DEGREES_PER_HOUR = 30
        private const val SECONDS_PER_MINUTE = 60
        private const val MINUTES_PER_HOUR = 60
        private const val SECONDS_PER_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE
        private const val FULL_ANGLE = 360
        private const val SECTOR_LENGTH = 5
        val secondsRange = 0..59
        val minutesRange = 0..59
        val hoursRange = 0..11

    }

    fun hourToAngle(hour: Hour, minute: Minute = Minute(0), second: Second = Second(0)): Angle {
        require(hour.value in hoursRange)
        val angle = (CLOCK_START_ANGLE + hour.value * DEGREES_PER_HOUR) % FULL_ANGLE
        val minuteAngle = minute.value.toFloat() / MINUTES_PER_HOUR * DEGREES_PER_HOUR
        val secondsAngle = second.value.toFloat() / SECONDS_PER_HOUR * DEGREES_PER_HOUR
        return Angle(angle + minuteAngle + secondsAngle)
    }

    fun minuteToAngle(minute: Minute, seconds: Second = Second(0)): Angle {
        require(minute.value in minutesRange)
        require(seconds.value in secondsRange)
        val angle = (CLOCK_START_ANGLE + minute.value * DEGREES_PER_MINUTE) % FULL_ANGLE
        val secondsAngle = seconds.value.toFloat() / SECONDS_PER_MINUTE * DEGREES_PER_MINUTE
        return Angle(angle.toFloat() + secondsAngle)
    }

    fun secondsToAngle(second: Second): Angle {
        require(second.value in secondsRange)
        val angle = (CLOCK_START_ANGLE + second.value * DEGREES_PER_SECOND) % FULL_ANGLE
        return Angle(angle.toFloat())
    }

    fun isSectorStart(minute: Minute): Boolean {
        return minute.value % SECTOR_LENGTH == 0
    }
}

@JvmInline
value class Minute(val value: Int) {
    init {
        require(value in AnalogClockGeometry.minutesRange)
    }
}

@JvmInline
value class Hour(val value: Int) {
    init {
        require(value in AnalogClockGeometry.hoursRange)
    }
}

@JvmInline
value class Second(val value: Int) {
    init {
        require(value in AnalogClockGeometry.secondsRange)
    }
}