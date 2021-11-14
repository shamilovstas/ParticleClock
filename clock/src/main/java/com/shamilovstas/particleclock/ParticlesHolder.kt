package com.shamilovstas.particleclock

import kotlin.math.sin
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

                val multiplier = getFraction(
                    start.value,
                    maxDistance.value - ParticleClock.BUBBLE_SPAWN_CENTER_MARGIN
                )
            val sin = sin(multiplier * Math.PI)
            particle.alpha = (sin * 255f).toInt()
            particle.radius = Radius(particle.initialRadius.value * sin.toFloat())
            particles.add(particle)
        }
    }

    companion object {
        const val PARTICLES_COUNT = 1000
    }
}