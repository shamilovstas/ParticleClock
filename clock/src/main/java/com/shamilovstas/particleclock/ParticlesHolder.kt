package com.shamilovstas.particleclock

import com.shamilovstas.particleclock.Bubble.Companion.INITIAL_RADIUS
import kotlin.math.max
import kotlin.random.Random

class ParticlesHolder {

    val bubbles = mutableListOf<Bubble>()

    fun init(radius: Float) {
        bubbles.clear()

        val minDistance = ParticleClock.BUBBLE_SPAWN_CENTER_MARGIN
        val maxDistance = radius - ParticleClock.OUTER_SECONDS_TRACK_MARGIN
        repeat(PARTICLES_COUNT) {
            val style = if (Random.nextBoolean()) Style.FILL else Style.STROKE
            val bubbleRadius = INITIAL_RADIUS
            val distanceFromCenter = Random.nextFloat(
                minDistance,
                maxDistance
            )
            val randomAngle = Random.nextFloat() * 360f

            val autoMove = Random.nextInt(0, 6) % 5 == 0

            val point = PolarPoint(distanceFromCenter)


            point.angle = randomAngle
            val bubble =
                Bubble(style = style, point = point, radius = bubbleRadius, autoMove = autoMove)

            bubble.setRadiusMultiplier(getDistancePercent(distanceFromCenter, maxDistance))
            bubbles.add(bubble)
        }
    }

    fun getDistancePercent(start: Float, end: Float): Double {
        require(start < end)
        return 1.0 - start / end;
    }

    companion object {
        const val PARTICLES_COUNT = 1000
    }
}