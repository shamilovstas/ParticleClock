package com.shamilovstas.particleclock.hand

import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.LinearInterpolator
import com.shamilovstas.particleclock.*
import com.shamilovstas.particleclock.model.angle.Angle
import com.shamilovstas.particleclock.model.angle.Sector
import com.shamilovstas.particleclock.model.point.CartesianPoint
import com.shamilovstas.particleclock.model.point.PolarPoint
import com.shamilovstas.particleclock.model.point.Radius
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private typealias HandRadius = Radius

class Hand(
    val radius: HandRadius,
    var sweepAngle: Angle = Angle(
        18f
    )
) {

    private val particles = mutableListOf<Particle>()

    init {
        repeat(PARTICLE_COUNT) {
            val radius = Random.nextFloat(0f, radius.value)
            val deviation = Random.nextFloat(-DEVIATION, DEVIATION)
            val style = if (Random.nextBoolean()) Style.FILL else Style.STROKE

            /* A particle drawn by a Hand is defined by a polar point (which is effectively an angle and
            some distance from some point O(x,y), which is called their center coordinate. Usually, all
            particles used in the view have their center set to O(0, 0) (which is the center of the clock).
            In practise, every particle is drawn in the following way: a particle gets its 'x' coordinate assigned,
            then canvas is rotated by the point's angle value. This way, the 'x' coordinate becomes the radius
            of the polar point describing the particle. But in case if we need to have particles with the same
            angle and radius but located at some distance from each other on a perpendicular placed through the vector
            of the particle, it effectively means its 'y' coordinate should be different from the particle
            we are shifting away from.
             */
            val coordinateCenter = CartesianPoint(y = deviation)
            val particle = Particle(
                coordinateCenter = coordinateCenter,
                point = PolarPoint(radius = Radius(radius)),
                style = style,
                radius = Radius(20f)
            )
            particle.radius = calculateParticleSize(particle, this.radius)
            particle.alpha = calculateParticleAlpha(particle, this.radius)

            particles.add(particle)
        }
    }

    val sector = Sector()
    var angle = Angle()
        set(value) {
            field = value
            val sweepHalf = sweepAngle / 2f
            sector.start = value - sweepHalf
            sector.end = value + sweepAngle - sweepHalf
        }

    fun startAnimation(invalidationCallback: () -> Unit) {
        infiniteAnimator(
            speed = 100,
            interpolator = LinearInterpolator(),
            onAnimationUpdate = { moveParticles(radius, invalidationCallback) }
        ).start()
    }

    fun draw(canvas: Canvas, paint: Paint) {
        if (angle.isInitialized()) {
            val count = canvas.save()
            canvas.rotate(angle.angle)
            particles.forEach {
                it.draw(canvas, paint)
            }
            canvas.restoreToCount(count)
        }
    }

    private fun moveParticles(
        maxRadius: Radius,
        callback: () -> Unit
    ) {
        for (particle in particles) {
            val newRadius = (particle.point.radius.value + 1) % maxRadius.value
            particle.point.radius = Radius(newRadius)
            particle.radius = calculateParticleSize(particle, this.radius)
            particle.alpha = calculateParticleAlpha(particle, this.radius)
        }
        callback.invoke()
    }

    private fun calculateParticleSize(particle: Particle, maxRadius: HandRadius): Radius {
        val distanceFraction = getFraction(particle.point.radius.value, maxRadius.value)
        val radiusMultiplier =
            if (distanceFraction > STABLE_RADIUS_THRESHOLD_PERCENT) STABLE_RADIUS_THRESHOLD_PERCENT else distanceFraction
        val sin = sin(radiusMultiplier * Math.PI)
        return Radius(particle.initialRadius.value * sin.toFloat())
    }

    private fun calculateParticleAlpha(particle: Particle, maxRadius: HandRadius): Int {
        val distanceFraction = getFraction(particle.point.radius.value, maxRadius.value)
        return (sin(distanceFraction * Math.PI) * 255).roundToInt()
    }


    companion object {
        const val PARTICLE_COUNT = 60
        private const val DEVIATION = 10f
        private const val STABLE_RADIUS_THRESHOLD_PERCENT = 0.5
    }
}