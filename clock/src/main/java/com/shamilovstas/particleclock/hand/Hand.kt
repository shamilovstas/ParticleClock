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

    // region preallocated
    private var polarPoint = PolarPoint(radius = radius)
    private var cartesianPoint = CartesianPoint()
    // endregion

    val paint = Paint().apply {
        color = Color.RED
        strokeWidth = 15f
    }

    val bubble = Bubble(PolarPoint(), Style.FILL, Radius(16f), true)

    val sector = Sector()
    var angle = Angle()
        set(value) {
            field = value
            val sweepHalf = sweepAngle / 2f
            sector.start = value - sweepHalf
            sector.end = value + sweepAngle - sweepHalf

            bubble.point.angle = angle
        }

    fun startAnimation() {
        animateFloat(0f to radius.value) {
            addUpdateListener(createParticlesMovementUpdater(radius))
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            duration = 2000
        }.start()
    }

    fun draw(canvas: Canvas, paint: Paint) {
        if (angle.isInitialized()) {
            polarPoint.angle = angle
            polarPoint.radius = radius
            polarPoint.toCartesian(cartesianPoint)

            canvas.drawLine(
                0f, 0f,
                cartesianPoint.x,
                cartesianPoint.y,
                paint
            )
            bubble.draw(canvas, this.paint)
        }
    }

    private fun createParticlesMovementUpdater(
        maxRadius: Radius
    ): ValueAnimator.AnimatorUpdateListener {
        return object : ValueAnimator.AnimatorUpdateListener {
            var previous = 0f
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val animatedValue = animation.animatedValue as Float
                val value = animatedValue - previous
                previous = animatedValue
                val point = bubble.point
                val radius = point.radius
                var newRadius = radius + value

                if (newRadius > maxRadius) {
                    newRadius = Radius(0f)
                }
                point.radius = newRadius
                clock.invalidate()
            }
        }
    }
}