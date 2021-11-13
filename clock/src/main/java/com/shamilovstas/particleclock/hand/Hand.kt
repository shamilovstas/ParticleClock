package com.shamilovstas.particleclock.hand

import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.LinearInterpolator
import com.shamilovstas.particleclock.*
import kotlin.random.Random


class Hand(
    val radius: Radius = Radius(0f),
    var sweepAngle: Angle = Angle(10f)
) {

    private val bubbles = mutableListOf<Bubble>()

    init {
        repeat(PARTICLE_COUNT) {
            val radius = Random.nextFloat(0f, radius.value)
            val deviation = Random.nextFloat(-DEVIATION, DEVIATION)
            val style = if (Random.nextBoolean()) Style.FILL else Style.STROKE
            val coordinateCenter = CartesianPoint(y = deviation)
            val bubble = Bubble(
                coordinateCenter = coordinateCenter,
                point = PolarPoint(radius = Radius(radius)),
                style = style,
                radius = Radius(20f)
            )
            bubbles.add(bubble)
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
            bubbles.forEach {
                it.draw(canvas, paint)
            }
            canvas.restoreToCount(count)
        }
    }

    private fun createParticlesMovementUpdater(
        maxRadius: Radius,
        callback: () -> Unit
    ) {
        for (bubble in bubbles) {
            val newRadius = (bubble.point.radius.value + 1) % maxRadius.value
            bubble.point.radius = Radius(newRadius)
        }
        callback.invoke()
    }

    companion object {
        const val PARTICLE_COUNT = 80
        private const val DEVIATION = 10f
    }
}