package com.shamilovstas.particleclock.background

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.shamilovstas.particleclock.*
import com.shamilovstas.particleclock.model.angle.Angle
import com.shamilovstas.particleclock.model.point.PolarPoint
import com.shamilovstas.particleclock.model.point.Radius
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

class ParticlesBackground(
    private val invalidateCallback: () -> Unit
) {

    private var isAnimationRunning = false

    private val particles = mutableListOf<Particle>()

    var allowedAngles: List<Int> = listOf()

    var radius: Radius =
        Radius(0f)
        set(value) {
            field = value
            init()
        }

    private fun init() {
        particles.clear()
        val minDistance =
            Radius(ParticleClock.BUBBLE_SPAWN_CENTER_MARGIN)
        val maxDistance: Radius = radius - ParticleClock.OUTER_SECONDS_TRACK_MARGIN
        repeat(PARTICLES_COUNT) {
            val style = if (Random.nextBoolean()) Style.FILL else Style.STROKE
            val bubbleRadius = Radius(10f)
            val distanceFromCenter = Radius(
                Random.nextFloat(
                    minDistance.value,
                    maxDistance.value
                )
            )
            val randomAngle = Random.nextFloat() * 360f

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
            particle.radius =
                Radius(particle.initialRadius.value * sin.toFloat())
            particles.add(particle)
        }
    }

    fun draw(canvas: Canvas, paint: Paint) {
        particles.forEach { it.draw(canvas, paint) }
    }

    fun backgroundParticlesPulse(): Animator {
        val maxRadius = radius - ParticleClock.OUTER_SECONDS_TRACK_MARGIN
        val pulseAnimator = ValueAnimator.ofInt(0, 25)
        pulseAnimator.duration = 300

        pulseAnimator.interpolator = AccelerateDecelerateInterpolator()

        pulseAnimator.addUpdateListener {
            createParticlesMovementUpdater(maxRadius)
        }
        return pulseAnimator
    }


    private fun createParticlesMovementUpdater(
        maxRadius: Radius
    ) {
        for (bubble in particles) {

            val point = bubble.point
            val radius = point.radius
            val nextRadius = radius + 1f
            point.radius = if (nextRadius > maxRadius) {
                val angle = allowedAngles.random() + Random.nextFloat(-1f, +1f)
                point.angle = Angle(abs(angle) % 360)
                Radius(ParticleClock.BUBBLE_SPAWN_CENTER_MARGIN)
            } else nextRadius

            val sizeMultiplier = getFraction(
                point.radius.value - ParticleClock.BUBBLE_SPAWN_CENTER_MARGIN,
                maxRadius.value - ParticleClock.BUBBLE_SPAWN_CENTER_MARGIN
            )
            val sin = sin(sizeMultiplier * Math.PI)
            bubble.alpha = (sin * 255f).toInt()
            bubble.radius =
                Radius(bubble.initialRadius.value * sin.toFloat())
        }
        invalidateCallback.invoke()
    }

    private fun startLinearBackgroundParticles() {
        isAnimationRunning = true
        // speed: 100px per second
        val animator = infiniteAnimator(100, LinearInterpolator()) {
            createParticlesMovementUpdater(
                radius - ParticleClock.OUTER_SECONDS_TRACK_MARGIN
            )
        }
        animator.start()
    }

    fun startAnimation() {
        startLinearBackgroundParticles()
    }

    companion object {
        const val PARTICLES_COUNT = 1000
    }
}