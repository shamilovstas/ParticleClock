package com.shamilovstas.particleclock

import androidx.annotation.VisibleForTesting
import kotlin.math.abs

@JvmInline
value class Angle(val angle: Float = Float.NaN) {

    companion object {
        val NOT_INITIALIZED = Angle(angle = Float.NaN)
        @VisibleForTesting
        val ANGLE_DELTA = 0.000001f
    }

    init {
        require(angle in 0f..360f || angle.isNaN()) { "Incorrect angle: $angle" }
    }

    operator fun minus(arg: Angle): Angle {
        require(isInitialized())
        require(arg.isInitialized())
        var result = this.angle - arg.angle
        if (result < 0f) {
            result += 360f
        }
        return Angle(result)
    }

    operator fun plus(arg: Angle): Angle {
        require(isInitialized())
        require(arg.isInitialized())
        var result = this.angle + arg.angle
        if (result > 360f) {
            result -= 360f
        }
        return Angle(result)
    }

    operator fun plus(float: Float): Angle {
        return this.plus(Angle(float))
    }

    operator fun minus(float: Float): Angle {
        return this.minus(Angle(float))
    }

    operator fun div(arg: Float): Angle {
        require(isInitialized())
        require(arg.isNaN().not())
        return Angle(this.angle / abs(arg))
    }

    operator fun compareTo(other: Angle): Int {
        return when {
            abs(this.angle - other.angle) < ANGLE_DELTA -> 0
            this.angle < other.angle -> -1
            else -> 1
        }
    }

    fun isInitialized(): Boolean {
        return this.angle.isNaN().not()
    }

    fun toRadians(): Double {
        return Math.toRadians(this.angle.toDouble())
    }
}