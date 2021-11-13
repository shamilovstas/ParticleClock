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

    private val bubbles = mutableListOf<CartesianPoint>()

    init {
        repeat(PARTICLE_COUNT) {
            val x = Random.nextFloat(0f, radius.value)
            val y = Random.nextFloat(-DEVIATION, DEVIATION)
            bubbles.add(CartesianPoint(x = x, y = y))
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
            bubbles.forEach { canvas.drawCircle(it.x, it.y, 20f, paint) }
            canvas.restoreToCount(count)
        }
    }

    private fun createParticlesMovementUpdater(
        maxRadius: Radius,
        callback: () -> Unit
    ) {
        for (bubble in bubbles) {
            bubble.x = (bubble.x + 1) % maxRadius.value
        }
        callback.invoke()
    }

    companion object {
        const val PARTICLE_COUNT = 80
        private const val DEVIATION = 10f
    }
}