package com.shamilovstas.particleclock.hand

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.animation.LinearInterpolator
import com.shamilovstas.particleclock.*
import com.shamilovstas.particleclock.ParticleClock.TemporaryHolders.cartesianPoint
import com.shamilovstas.particleclock.ParticleClock.TemporaryHolders.polarPoint
import kotlin.math.abs
import kotlin.random.Random


class Hand(
    val radius: Radius = Radius(0f),
    var sweepAngle: Angle = Angle(10f),
    val clock: ParticleClock
) {

    val bubbles = mutableListOf<CartesianPoint>()

    init {
        repeat(PARTICLE_COUNT) {
            val x = Random.nextFloat(0f, radius.value)
            val y = Random.nextFloat(-10f, 10f)
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

    fun startAnimation() {
        animateFloat(0f to radius.value) {
            addUpdateListener(createParticlesMovementUpdater(radius))
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            duration = 1000
        }.start()
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
        maxRadius: Radius
    ): ValueAnimator.AnimatorUpdateListener {
        return ValueAnimator.AnimatorUpdateListener {
            for (bubble in bubbles) {
                bubble.x = (bubble.x + 1) % maxRadius.value
            }
            clock.invalidate()
        }
    }

    companion object {
        const val PARTICLE_COUNT = 80
    }
}