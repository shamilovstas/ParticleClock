package com.shamilovstas.particleclock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toRect
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
        this.strokeWidth = 6.0f
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
    var clockCircle = Circle()
    var innerCircle = Circle()

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
        val size = width.coerceAtLeast(height)
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

            val radius = if (isHour) 10 else 6
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
        canvas.drawArc(innerCircle.boundingRectF, 0f, 6f, false, secondsIndicatorPaint)
        TemporaryHolders.refresh()
        // endregion
    }

    private fun isSectorStart(minute: Int): Boolean {
        return minute % SECTOR_SIZE == 0
    }

    companion object {
        const val MIN_SIZE = 1000
        const val MINUTES_IN_HOUR = 60
        const val SECTOR_SIZE = 5
        const val DEGREE_PER_SEGMENT = 360 / 60
        const val INNER_CIRCLE_MARGIN = 100
    }
}
