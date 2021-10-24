package com.shamilovstas.particleclock

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import java.time.LocalTime
import java.time.temporal.ChronoField
import kotlin.math.min
import kotlin.random.Random

class ParticleClock @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    // region Paints
    val hourPaint = Paint().apply {
        this.color = Color.BLUE
        this.strokeWidth = 10.0f
        this.style = Paint.Style.FILL
    }

    val minutePaint = Paint().apply {
        this.color = Color.BLUE
        this.strokeWidth = 4.0f
        this.style = Paint.Style.STROKE
    }

    val secondsTrackPaint = Paint().apply {
        this.color = Color.BLUE
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    val secondsIndicatorPaint: Paint = Paint().apply {
        this.color = Color.BLUE
        strokeWidth = 12f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    val bubblePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 4f
    }

    // endregion
    // region Objects
    var secondsHandAngle = Float.NaN
    var minutesHandAngle = Float.NaN
    var hoursHandAngle = Float.NaN

    var clockCircle = Circle()

    val analogClockGeometry = AnalogClockGeometry()
    val bubbles = mutableListOf<Bubble>()

    object TemporaryHolders {
        fun refresh() {
            circle.refresh()
            cartesianPoint.refresh()
            polarPoint.refresh()
        }

        var circle = Circle()
        var cartesianPoint = CartesianPoint()
        val polarPoint = PolarPoint()
    }
    // endregion

    // region Size calculations
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = resolveSize(MIN_SIZE, widthMeasureSpec)
        val height = resolveSize(MIN_SIZE, heightMeasureSpec)
        val size = width.coerceAtMost(height)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val cx = w / 2
        val cy = h / 2
        clockCircle = Circle(CartesianPoint(cx, cy), min(cx, cy) - 40f)
        bubbles.clear()
        repeat(1000) {
            val style = if (Random.nextBoolean()) Style.FILL else Style.STROKE
            val bubbleRadius = Random.nextInt(4, 14).toFloat()
            val randomRadius = Random.nextFloat(
                INNER_SECONDS_TRACK_MARGIN,
                clockCircle.radius - OUTER_SECONDS_TRACK_MARGIN
            )
            val randomAngle = Random.nextFloat() * 360f

            val autoMove = Random.nextInt(0, 6) % 5 == 0

            val point = PolarPoint(randomAngle, randomRadius)
            val bubble = Bubble(style = style, point = point, radius = bubbleRadius, autoMove = autoMove)

            bubbles.add(bubble)
        }
    }
    // endregion

    private fun pulse(): Animator {

        val maxRadius = clockCircle.radius - OUTER_SECONDS_TRACK_MARGIN
        val pulseAnimator = ValueAnimator.ofInt(0, 150)
        val linearAnimator = ValueAnimator.ofInt(0, 70)
        linearAnimator.duration = 1000
        pulseAnimator.duration = 1000

        linearAnimator.interpolator = LinearInterpolator()
        pulseAnimator.interpolator = AccelerateDecelerateInterpolator()

        pulseAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            var previous = 0
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val animatedValue = animation.animatedValue as Int
                val value = animatedValue - previous
                previous = animatedValue
                for (bubble in bubbles) {
                    val point = bubble.point
                    val radius = point.radius
                    var newRadius = radius + value

                    if (newRadius > maxRadius) {
                        newRadius = INNER_SECONDS_TRACK_MARGIN
                    }
                    point.radius = newRadius
                }
                invalidate()
            }
        })
        linearAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            var previous = 0
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val animatedValue = animation.animatedValue as Int
                val value = animatedValue - previous
                previous = animatedValue
                for (bubble in bubbles) {
                    if (bubble.autoMove.not()) continue
                    val point = bubble.point
                    val radius = point.radius
                    var newRadius = radius + value

                    if (newRadius > maxRadius) {
                        newRadius = INNER_SECONDS_TRACK_MARGIN
                    }
                    point.radius = newRadius
                }
                invalidate()
            }
        })
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(linearAnimator, pulseAnimator)
        return animatorSet
    }

    override fun onDraw(canvas: Canvas) {
        val radius = clockCircle.radius
        // region 1. Drawing outer clock contour (minutes and hours indicators)
        drawMinutesIndicators(canvas)
        ParticleClock.TemporaryHolders.refresh()
        // endregion
        // region 2. Drawing the seconds track (may be referred as the 'inner circle')
        drawSecondsTrack(
            canvas,
            radius - OUTER_SECONDS_TRACK_MARGIN,
            OUTER_SECONDS_INDICATOR_SWEEP_ANGLE
        )
        ParticleClock.TemporaryHolders.refresh()
        drawSecondsTrack(canvas, INNER_SECONDS_TRACK_MARGIN, INNER_SECONDS_INDICATOR_SWEEP_ANGLE)
        ParticleClock.TemporaryHolders.refresh()

        drawDebugMinuteHand(canvas)
        TemporaryHolders.refresh()
        drawDebugHourHand(canvas)
        TemporaryHolders.refresh()
        // endregion

        bubbles.forEach { it.draw(canvas, bubblePaint, clockCircle.center) }
    }

    private fun drawDebugHourHand(canvas: Canvas) {
        if (!hoursHandAngle.isNaN()) {
            val center = clockCircle.center
            val circle = TemporaryHolders.circle.apply {
                radius = 400f
                this.center.copyFrom(clockCircle.center)
            }
            circle.getPoint(hoursHandAngle, TemporaryHolders.cartesianPoint)
            val end = TemporaryHolders.cartesianPoint
            canvas.drawLine(
                center.x,
                center.y,
                end.x,
                end.y,
                hourPaint
            )
        }
    }

    private fun drawDebugMinuteHand(canvas: Canvas) {
        if (!minutesHandAngle.isNaN()) {
            val center = clockCircle.center

            clockCircle.getPoint(minutesHandAngle, TemporaryHolders.cartesianPoint)
            val end = TemporaryHolders.cartesianPoint
            canvas.drawLine(
                center.x,
                center.y,
                end.x,
                end.y,
                minutePaint
            )
        }
    }

    fun setTime(localDate: LocalTime) {
        val hours = localDate.get(ChronoField.HOUR_OF_AMPM)
        val seconds = localDate.get(ChronoField.SECOND_OF_MINUTE)
        val minutes = localDate.get(ChronoField.MINUTE_OF_HOUR)
        runSecondsTrackAnimation(seconds)
        setMinuteHandAngle(minutes, seconds)
        setHourHandAngle(hours, minutes, seconds)
        invalidate()
    }

    private fun drawMinutesIndicators(canvas: Canvas) {
        for (minute in 0 until MINUTES_IN_HOUR) {
            val degree = minute * DEGREE_PER_SEGMENT
            val point = TemporaryHolders.cartesianPoint
            clockCircle.getPoint(degree.toFloat(), point)
            val isHour = analogClockGeometry.isSectorStart(Minute(minute))

            val radius = if (isHour) 20f else 10f
            val paint = if (isHour) hourPaint else minutePaint
            val indicator = TemporaryHolders.circle
            indicator.center.copyFrom(point)
            indicator.radius = radius
            indicator.draw(canvas, paint)
        }
    }

    private fun drawSecondsTrack(canvas: Canvas, radius: Float, indicatorAngle: Float) {
        val circle = TemporaryHolders.circle
        circle.center.copyFrom(clockCircle.center)
        circle.radius = radius
        circle.draw(canvas, secondsTrackPaint)

        if (secondsHandAngle.isNaN().not()) {
            val angle = secondsHandAngle - indicatorAngle / 2f
            canvas.drawArc(
                circle.boundingRectF,
                angle,
                indicatorAngle,
                false,
                secondsIndicatorPaint
            )
        }
    }

    private fun runSecondsTrackAnimation(seconds: Int) {
        var angle = analogClockGeometry.secondsToAngle(Second(seconds))
        if (secondsHandAngle.isNaN()) {
            secondsHandAngle = angle
        } else {
            val isNewLap = angle < secondsHandAngle
            if (isNewLap) angle += 360
            val secondsAnimation = animateFloat(secondsHandAngle to angle) {
                addUpdateListener {
                    val value = it.animatedValue as Float
                    secondsHandAngle = value
                    invalidate()
                }
                addListener(animationAdapter(onEnd = {
                    if (isNewLap) secondsHandAngle = 360 - secondsHandAngle
                }))
            }
            val animator = AnimatorSet()
            animator.playTogether(secondsAnimation, pulse())
            animator.start()
        }
    }

    private fun setHourHandAngle(hour: Int, minute: Int, seconds: Int) {
        hoursHandAngle =
            analogClockGeometry.hourToAngle(Hour(hour), Minute(minute), Second(seconds))
    }

    private fun setMinuteHandAngle(minute: Int, seconds: Int) {
        minutesHandAngle = analogClockGeometry.minuteToAngle(Minute(minute), Second(seconds))
    }

    companion object {
        const val MIN_SIZE = 1000
        const val MINUTES_IN_HOUR = 60
        const val DEGREE_PER_SEGMENT = 360 / 60
        const val OUTER_SECONDS_TRACK_MARGIN = 100
        const val INNER_SECONDS_TRACK_MARGIN = 80f
        const val OUTER_SECONDS_INDICATOR_SWEEP_ANGLE = 4f
        const val INNER_SECONDS_INDICATOR_SWEEP_ANGLE = 40f
    }

}
