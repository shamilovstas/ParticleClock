package com.shamilovstas.particleclock

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.shamilovstas.particleclock.background.ParticlesBackground
import com.shamilovstas.particleclock.hand.Hand
import java.time.LocalTime
import java.time.temporal.ChronoField
import kotlin.math.min

class ParticleClock @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    private var isInitialized = false
    // region Paints


    val particlesPaint = Paint().apply {
        this.color = Color.BLUE
        this.isAntiAlias = true
        this.strokeWidth = 2f
    }
    val hourPaint = Paint().apply {
        this.color = Color.BLUE
        this.strokeWidth = 10.0f
        this.isAntiAlias = true
        this.style = Paint.Style.FILL
    }

    val minutePaint = Paint().apply {
        this.color = Color.BLUE
        this.strokeWidth = 4.0f
        this.isAntiAlias = true
        this.style = Paint.Style.STROKE
    }

    val secondsTrackPaint = Paint().apply {
        this.color = Color.BLUE
        strokeWidth = 2f
        this.isAntiAlias = true
        style = Paint.Style.STROKE
    }

    val secondsIndicatorPaint: Paint = Paint().apply {
        this.color = Color.BLUE
        strokeWidth = 12f
        this.isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val particlesBackground =
        ParticlesBackground(true) { this.invalidate() }


    // endregion
    // region Objects
    var secondsHandAngle = Angle()
    var minutesHand = Hand(radius = Radius(600f))
    var hoursHand = Hand(radius = Radius(400f))


    var clockRadius: Radius = Radius()
    var center: CartesianPoint = CartesianPoint()

    val analogClockGeometry = AnalogClockGeometry()

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
        center = CartesianPoint(cx, cy)
        clockRadius = Radius(min(cx, cy) - 40f)
        particlesBackground.radius = clockRadius
    }
    // endregion

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.translate(center.x, center.y)
        // region 1. Drawing outer clock contour (minutes and hours indicators)
        drawMinutesIndicators(canvas)
        TemporaryHolders.refresh()
        // endregion
        // region 2. Drawing the seconds track (may be referred as the 'inner circle')
        drawSecondsTrack(
            canvas,
            clockRadius - OUTER_SECONDS_TRACK_MARGIN,
            Angle(OUTER_SECONDS_INDICATOR_SWEEP_ANGLE)
        )
        TemporaryHolders.refresh()
        drawSecondsTrack(
            canvas,
            Radius(INNER_SECONDS_TRACK_MARGIN),
            Angle(INNER_SECONDS_INDICATOR_SWEEP_ANGLE)
        )
        TemporaryHolders.refresh()

        minutesHand.draw(canvas, particlesPaint)
        TemporaryHolders.refresh()
        hoursHand.draw(canvas, particlesPaint)
        TemporaryHolders.refresh()
        // endregion
        particlesBackground.draw(canvas, particlesPaint)
    }

    fun setTime(localDate: LocalTime) {
        val hours = localDate.get(ChronoField.HOUR_OF_AMPM)
        val seconds = localDate.get(ChronoField.SECOND_OF_MINUTE)
        val minutes = localDate.get(ChronoField.MINUTE_OF_HOUR)
        runSecondsAnimation(seconds)
        setMinuteHandAngle(minutes, seconds)
        setHourHandAngle(hours, minutes, seconds)

        val minuteHandAngleRange = minutesHand.sector.asRange()
        val hourHandAngleRange = hoursHand.sector.asRange()
        particlesBackground.allowedAngles = (0..360) - minuteHandAngleRange - hourHandAngleRange
        init()
        invalidate()
    }

    private fun drawMinutesIndicators(canvas: Canvas) {
        for (minute in 0 until MINUTES_IN_HOUR) {
            val angle = Angle((minute * DEGREE_PER_SEGMENT).toFloat())
            val indicatorCoordinate = TemporaryHolders.polarPoint.also {
                it.radius = clockRadius
                it.angle = angle
            }
            val isHour = analogClockGeometry.isSectorStart(Minute(minute))
            val radius = if (isHour) 20f else 10f
            val paint = if (isHour) hourPaint else minutePaint
            indicatorCoordinate.drawCircle(Radius(radius), canvas, paint)
        }
    }

    private fun drawSecondsTrack(canvas: Canvas, radius: Radius, indicatorAngle: Angle) {
        val circle = TemporaryHolders.circle
        circle.radius = radius
        circle.draw(canvas, secondsTrackPaint)

        if (secondsHandAngle.isInitialized()) {
            val angle = secondsHandAngle - indicatorAngle / 2f
            canvas.drawArc(
                circle.boundingRectF,
                angle.angle,
                indicatorAngle.angle,
                false,
                secondsIndicatorPaint
            )
        }
    }

    private fun runSecondsAnimation(seconds: Int) {
        var angle = analogClockGeometry.secondsToAngle(Second(seconds))
        if (secondsHandAngle.isInitialized().not()) {
            secondsHandAngle = angle
        } else {
            val isNewLap = angle < secondsHandAngle
            if (isNewLap) angle += Angle(360f)
            val secondsAnimation = animateFloat(secondsHandAngle.angle to angle.angle) {
                addUpdateListener {
                    val value = it.animatedValue as Float
                    secondsHandAngle = Angle(value)
                    invalidate()
                }
                addListener(animationAdapter(onEnd = {
                    if (isNewLap) secondsHandAngle = Angle(360f) - secondsHandAngle
                }))
            }
            val animator = AnimatorSet()
            animator.playTogether(secondsAnimation, particlesBackground.backgroundParticlesPulse())
            animator.start()
        }
    }

    private fun setHourHandAngle(hour: Int, minute: Int, seconds: Int) {
        hoursHand.angle =
            analogClockGeometry.hourToAngle(Hour(hour), Minute(minute), Second(seconds))
    }

    private fun setMinuteHandAngle(minute: Int, seconds: Int) {
        minutesHand.angle = analogClockGeometry.minuteToAngle(Minute(minute), Second(seconds))
    }

    private fun init() {
        if (!isInitialized) {
            isInitialized = true
            hoursHand.startAnimation { invalidate() }
            minutesHand.startAnimation { invalidate() }
            particlesBackground.startAnimation()
        }
    }


    companion object {
        const val MIN_SIZE = 1000
        const val MINUTES_IN_HOUR = 60
        const val DEGREE_PER_SEGMENT = 360 / 60
        const val OUTER_SECONDS_TRACK_MARGIN = 50f
        const val INNER_SECONDS_TRACK_MARGIN = 80f
        const val BUBBLE_SPAWN_CENTER_MARGIN = INNER_SECONDS_TRACK_MARGIN
        const val OUTER_SECONDS_INDICATOR_SWEEP_ANGLE = 4f
        const val INNER_SECONDS_INDICATOR_SWEEP_ANGLE = 40f
    }
}
