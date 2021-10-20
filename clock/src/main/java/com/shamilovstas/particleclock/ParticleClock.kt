package com.shamilovstas.particleclock

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.time.LocalTime
import java.time.temporal.ChronoField
import kotlin.math.min

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

    val handPaint = Paint().apply {
        this.color = Color.BLUE
        strokeWidth = 12f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    // endregion
    // region Objects
    var secondsHandAngle = Float.NaN
    var minutesHandAngle = Float.NaN
    var hoursHandAngle = Float.NaN

    var clockCircle = Circle()
    var innerCircle = Circle()
    val analogClockGeometry = AnalogClockGeometry()

    object TemporaryHolders {
        fun refresh() {
            circle.refresh()
            point.refresh()
        }

        var circle = Circle()
        var point = Point()
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
        clockCircle = Circle(Point(cx, cy), min(cx, cy) - 40)
    }
    // endregion

    override fun onDraw(canvas: Canvas) {
        val radius = clockCircle.radius
        // region 1. Drawing outer clock contour (minutes and hours indicators)
        drawMinutesIndicators(canvas)
        TemporaryHolders.refresh()
        // endregion
        // region 2. Drawing the seconds track (may be referred as the 'inner circle')
        drawSecondsTrack(canvas, radius - OUTER_SECONDS_TRACK_MARGIN, OUTER_SECONDS_INDICATOR_SWEEP_ANGLE)
        TemporaryHolders.refresh()
        drawSecondsTrack(canvas, INNER_SECONDS_TRACK_MARGIN, INNER_SECONDS_INDICATOR_SWEEP_ANGLE)
        TemporaryHolders.refresh()

        drawDebugMinuteHand(canvas)
        drawDebugHourHand(canvas)
        // endregion
    }

    private fun drawDebugHourHand(canvas: Canvas) {
        if (!hoursHandAngle.isNaN()) {
            val center = clockCircle.center
            val circle = TemporaryHolders.circle.apply {
                radius = 400
                this.center = clockCircle.center
            }
            circle.getPoint(hoursHandAngle, TemporaryHolders.point)
            val end = TemporaryHolders.point
            canvas.drawLine(center.x.toFloat(), center.y.toFloat(), end.x.toFloat(), end.y.toFloat(), hourPaint)
        }
    }

    private fun drawDebugMinuteHand(canvas: Canvas) {
        if (!minutesHandAngle.isNaN()) {
            val center = clockCircle.center
            clockCircle.getPoint(minutesHandAngle, TemporaryHolders.point)
            val end = TemporaryHolders.point
            canvas.drawLine(center.x.toFloat(), center.y.toFloat(), end.x.toFloat(), end.y.toFloat(), minutePaint)
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
            val point = TemporaryHolders.point
            clockCircle.getPoint(degree.toFloat(), point)
            val isHour = analogClockGeometry.isSectorStart(Minute(minute))

            val radius = if (isHour) 20 else 10
            val paint = if (isHour) hourPaint else minutePaint
            val indicator = TemporaryHolders.circle
            indicator.center = point
            indicator.radius = radius
            indicator.draw(canvas, paint)
        }

        TemporaryHolders.refresh()
    }

    private fun drawSecondsTrack(canvas: Canvas, radius: Int, indicatorAngle: Float) {
        innerCircle.center = clockCircle.center
        innerCircle.radius = radius
        innerCircle.draw(canvas, secondsTrackPaint)

        if (secondsHandAngle.isNaN().not()) {
            val angle = secondsHandAngle - indicatorAngle / 2f
            canvas.drawArc(innerCircle.boundingRectF, angle, indicatorAngle, false, secondsIndicatorPaint)
        }
    }

    private fun runSecondsTrackAnimation(seconds: Int) {
        var angle = analogClockGeometry.secondsToAngle(Second(seconds))
        if (secondsHandAngle.isNaN()) {
            secondsHandAngle = angle
        } else {
            val isNewLap = angle < secondsHandAngle
            if (isNewLap) angle += 360
            animateFloat(secondsHandAngle to angle) {
                addUpdateListener {
                    val value = it.animatedValue as Float
                    secondsHandAngle = value
                    invalidate()
                }
                addListener(animationAdapter(onEnd = { if (isNewLap) secondsHandAngle = 360 - secondsHandAngle }))
            }.start()
        }
    }

    private fun setHourHandAngle(hour: Int, minute: Int, seconds: Int) {
        hoursHandAngle = analogClockGeometry.hourToAngle(Hour(hour), Minute(minute), Second(seconds))
    }

    private fun setMinuteHandAngle(minute: Int, seconds: Int) {
        minutesHandAngle = analogClockGeometry.minuteToAngle(Minute(minute), Second(seconds))
    }

    companion object {
        const val MIN_SIZE = 1000
        const val MINUTES_IN_HOUR = 60
        const val DEGREE_PER_SEGMENT = 360 / 60
        const val OUTER_SECONDS_TRACK_MARGIN = 100
        const val INNER_SECONDS_TRACK_MARGIN = 80
        const val OUTER_SECONDS_INDICATOR_SWEEP_ANGLE = 4f
        const val INNER_SECONDS_INDICATOR_SWEEP_ANGLE = 40f
    }

}

fun animateFloat(pair: Pair<Float, Float>, block: ValueAnimator.() -> Unit): ValueAnimator {
    val animator = ValueAnimator.ofFloat(pair.first, pair.second)
    block(animator)
    return animator
}

fun animationAdapter(
    onStart: (Animator) -> Unit = {},
    onEnd: (Animator) -> Unit = {},
    onCancel: (Animator) -> Unit = {},
    onRepeat: (Animator) -> Unit = {},
): Animator.AnimatorListener {
    return object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {
            onStart(animation!!)
        }

        override fun onAnimationEnd(animation: Animator?) {
            onEnd(animation!!)
        }

        override fun onAnimationCancel(animation: Animator?) {
            onCancel(animation!!)
        }

        override fun onAnimationRepeat(animation: Animator?) {
            onRepeat(animation!!)
        }
    }
}
