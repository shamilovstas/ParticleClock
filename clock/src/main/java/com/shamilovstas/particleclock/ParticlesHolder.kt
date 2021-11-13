package com.shamilovstas.particleclock

import com.shamilovstas.particleclock.Bubble.Companion.INITIAL_RADIUS
import kotlin.random.Random

class ParticlesHolder {

    val bubbles = mutableListOf<Bubble>()

    fun init(radius: Radius) {
        bubbles.clear()

        val minDistance = Radius(ParticleClock.BUBBLE_SPAWN_CENTER_MARGIN)
        val maxDistance: Radius = radius - ParticleClock.OUTER_SECONDS_TRACK_MARGIN
        repeat(PARTICLES_COUNT) {
            val style = if (Random.nextBoolean()) Style.FILL else Style.STROKE
            val bubbleRadius = Radius(INITIAL_RADIUS)
            val distanceFromCenter = Radius(Random.nextFloat(minDistance.value, maxDistance.value))
            val randomAngle = Random.nextFloat() * 360f

            val autoMove = Random.nextInt(0, 6) % 5 == 0

            val point = PolarPoint(distanceFromCenter)


            point.angle = Angle(randomAngle)
            val bubble =
                Bubble(point = point, style = style, radius = bubbleRadius)


            val start = distanceFromCenter - ParticleClock.BUBBLE_SPAWN_CENTER_MARGIN

            bubble.setRadiusMultiplier(
                getDistancePercent(
                    start,
                    maxDistance - ParticleClock.BUBBLE_SPAWN_CENTER_MARGIN
                )
            )
            bubbles.add(bubble)
        }
    }

    fun getDistancePercent(start: Radius, end: Radius): Double {
        require(start <= end)
        return 1.0 - (start / end).value;
    }

    companion object {
        const val PARTICLES_COUNT = 1000
    }
}