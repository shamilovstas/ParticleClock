package com.shamilovstas.particleclock

import kotlin.random.Random

class ParticlesHolder {

    val bubbles = mutableListOf<Bubble>()

    fun init(radius: Float) {
        bubbles.clear()

        repeat(PARTICLES_COUNT) {
            val style = if (Random.nextBoolean()) Style.FILL else Style.STROKE
            val bubbleRadius = INITIAL_RADIUS
            val randomRadius = Random.nextFloat(
                ParticleClock.INNER_SECONDS_TRACK_MARGIN,
                radius - ParticleClock.OUTER_SECONDS_TRACK_MARGIN
            )
            val randomAngle = Random.nextFloat() * 360f

            val autoMove = Random.nextInt(0, 6) % 5 == 0

            val point = PolarPoint(randomRadius)

            point.angle = randomAngle
            val bubble =
                Bubble(style = style, point = point, radius = bubbleRadius, autoMove = autoMove)

            bubbles.add(bubble)
        }
    }

    companion object {
        const val PARTICLES_COUNT = 1000
        const val INITIAL_RADIUS = 10f
    }
}