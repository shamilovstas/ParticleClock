package com.shamilovstas.particleclock.hand

import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.LinearInterpolator
import com.shamilovstas.particleclock.*
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random


class Hand(
    val radius: Radius = Radius(0f),
    var sweepAngle: Angle = Angle(18f)
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
            onAnimationUpdate = { createParticlesMovementUpdater(radius, invalidationCallback) }
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

    private fun createParticlesMovementUpdater(
        maxRadius: Radius,
        callback: () -> Unit
    ) {
        for (bubble in particles) {
            val newRadius = (bubble.point.radius.value + 1) % maxRadius.value
            bubble.point.radius = Radius(newRadius)
            val distanceFraction = getFraction(bubble.point.radius.value, radius.value)
            val radiusMultiplier = if (distanceFraction > 0.5) 0.5 else distanceFraction
            val sin = sin(radiusMultiplier * Math.PI)
            bubble.radius = Radius(bubble.initialRadius.value * sin.toFloat())
            bubble.alpha = (sin(distanceFraction * Math.PI) * 255).roundToInt()
        }
        callback.invoke()
    }

    companion object {
        const val PARTICLE_COUNT = 60
        private const val DEVIATION = 10f
    }
}