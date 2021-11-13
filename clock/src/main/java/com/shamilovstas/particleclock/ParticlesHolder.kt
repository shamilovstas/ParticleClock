package com.shamilovstas.particleclock

import kotlin.random.Random

class ParticlesHolder {

    val particles = mutableListOf<Particle>()

    fun init(radius: Radius) {
        particles.clear()

        val minDistance = Radius(ParticleClock.BUBBLE_SPAWN_CENTER_MARGIN)
        val maxDistance: Radius = radius - ParticleClock.OUTER_SECONDS_TRACK_MARGIN
        repeat(PARTICLES_COUNT) {
            val style = if (Random.nextBoolean()) Style.FILL else Style.STROKE
            val bubbleRadius = Radius(10f)
            val distanceFromCenter = Radius(Random.nextFloat(minDistance.value, maxDistance.value))
            val randomAngle = Random.nextFloat() * 360f

            val autoMove = Random.nextInt(0, 6) % 5 == 0

            val point = PolarPoint(distanceFromCenter)


            point.angle = Angle(randomAngle)
            val particle =
                Particle(point = point, style = style, radius = bubbleRadius)


            val start = distanceFromCenter - ParticleClock.BUBBLE_SPAWN_CENTER_MARGIN

            particle.setDistanceMultiplier(
                getFraction(
                    start.value,
                    maxDistance.value - ParticleClock.BUBBLE_SPAWN_CENTER_MARGIN
                )
            )
            particles.add(particle)
        }
    }

    companion object {
        const val PARTICLES_COUNT = 1000
    }
}