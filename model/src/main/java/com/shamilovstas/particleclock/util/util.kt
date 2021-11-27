package com.shamilovstas.particleclock.util

import kotlin.random.Random

fun Random.Default.nextFloat(from: Float, to: Float): Float {
    return from + nextFloat() * (to - from)
}

fun getFraction(start: Float, end: Float): Double {
    if (end <= 0) return 0.0
    require(start <= end)
    return 1.0 - (start / end)
}