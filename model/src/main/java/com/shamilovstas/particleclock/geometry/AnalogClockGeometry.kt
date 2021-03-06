package com.shamilovstas.particleclock.geometry

import com.shamilovstas.particleclock.model.angle.Angle
import com.shamilovstas.particleclock.model.time.Hour
import com.shamilovstas.particleclock.model.time.Minute
import com.shamilovstas.particleclock.model.time.Second

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
        val angle = (CLOCK_START_ANGLE + hour.value * DEGREES_PER_HOUR) % FULL_ANGLE
        val minuteAngle = minute.value.toFloat() / MINUTES_PER_HOUR * DEGREES_PER_HOUR
        val secondsAngle = second.value.toFloat() / SECONDS_PER_HOUR * DEGREES_PER_HOUR
        return Angle(angle + minuteAngle + secondsAngle)
    }

    fun minuteToAngle(minute: Minute, seconds: Second = Second(0)): Angle {
        val angle = (CLOCK_START_ANGLE + minute.value * DEGREES_PER_MINUTE) % FULL_ANGLE
        val secondsAngle = seconds.value.toFloat() / SECONDS_PER_MINUTE * DEGREES_PER_MINUTE
        return Angle(angle.toFloat() + secondsAngle)
    }

    fun secondsToAngle(second: Second): Angle {
        val angle = (CLOCK_START_ANGLE + second.value * DEGREES_PER_SECOND) % FULL_ANGLE
        return Angle(angle.toFloat())
    }

    fun isSectorStart(minute: Minute): Boolean {
        return minute.value % SECTOR_LENGTH == 0
    }
}





