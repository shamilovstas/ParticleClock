package com.shamilovstas.particleclock

import kotlin.random.Random

fun Random.Default.nextFloat(from: Float, to: Float): Float {
    return from + nextFloat() * (to - from)
}