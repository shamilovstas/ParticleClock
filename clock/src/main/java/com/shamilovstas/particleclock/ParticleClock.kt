package com.shamilovstas.particleclock

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.graphics.toRect
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoField
import java.time.temporal.TemporalField
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

    // endregion
    // region Objects
    var currentAngle = -1f
    var clockCircle = Circle()
    var innerCircle = Circle()
    val analogClock = AnalogClockGeometry(clockCircle)

    object TemporaryHolders {
        fun refresh() {

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
        // region 1. Drawing outer clock contour (minutes and hours indicators)
        for (minute in 0 until MINUTES_IN_HOUR) {
            val degree = minute * DEGREE_PER_SEGMENT
            val point = TemporaryHolders.point
            clockCircle.getPoint(degree.toFloat(), point)
            val isHour = isSectorStart(minute)

            val radius = if (isHour) 20 else 10
            val paint = if (isHour) hourPaint else minutePaint
            val indicator = TemporaryHolders.circle
            indicator.center = point
            indicator.radius = radius
            indicator.draw(canvas, paint)
        }

        TemporaryHolders.refresh()
        // endregion
        // region 2. Drawing the seconds track (may be referred as the 'inner circle')
        innerCircle.center = clockCircle.center
        innerCircle.radius = clockCircle.radius - INNER_CIRCLE_MARGIN
        innerCircle.draw(canvas, secondsTrackPaint)

        if (currentAngle > 0) {
            val angle = currentAngle - SECONDS_INDICATOR_SWEEP_ANGLE / 2f
            canvas.drawArc(innerCircle.boundingRectF, angle, SECONDS_INDICATOR_SWEEP_ANGLE, false, secondsIndicatorPaint)
        }
        TemporaryHolders.refresh()
        // endregion
    }

    private fun isSectorStart(minute: Int): Boolean {
        return minute % SECTOR_SIZE == 0
    }

    fun setTime(localDate: LocalTime) {
        val seconds = localDate.get(ChronoField.SECOND_OF_MINUTE)
        runSecondsTrackAnimation(seconds)
    }

    private fun runSecondsTrackAnimation(seconds: Int) {
        val angle = analogClock.minuteToAngle(seconds).toFloat()
        if (currentAngle > 0) {
            animateFloat(currentAngle to angle) {
                addUpdateListener {
                    currentAngle = it.animatedValue as Float
                    invalidate()
                }
            }.start()
        }
        currentAngle = angle
    }

    companion object {
        const val MIN_SIZE = 1000
        const val MINUTES_IN_HOUR = 60
        const val SECTOR_SIZE = 5
        const val DEGREE_PER_SEGMENT = 360 / 60
        const val INNER_CIRCLE_MARGIN = 100
        const val SECONDS_INDICATOR_SWEEP_ANGLE = 4f
    }

}

fun animateFloat(pair: Pair<Float, Float>, block: ValueAnimator.() -> Unit): ValueAnimator {
    val animator = ValueAnimator.ofFloat(pair.first, pair.second)
    block(animator)
    return animator
}
