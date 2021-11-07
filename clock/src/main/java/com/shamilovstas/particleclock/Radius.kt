package com.shamilovstas.particleclock

import android.util.Log
import kotlin.math.max
import kotlin.math.min

@JvmInline
value class Radius(val value: Float = 0f) {
    init {
        require(value >= 0f) { "Failed requirement: $value"}
    }

    operator fun minus(float: Float): Radius {
        return Radius(max(this.value - float, 0f))
    }

    operator fun minus(radius: Radius): Radius {
        return this.minus(radius.value)
    }

    operator fun plus(float: Float): Radius {
        return Radius(this.value + float)
    }

    operator fun plus(radius: Radius): Radius {
        return this.plus(radius.value)
    }

    operator fun compareTo(other: Radius): Int {
        return this.value.compareTo(other.value)
    }

    operator fun div(float: Float): Radius {
        return Radius(this.value / float)
    }

    operator fun div(radius: Radius): Radius {
        return this.div(radius.value)
    }
}