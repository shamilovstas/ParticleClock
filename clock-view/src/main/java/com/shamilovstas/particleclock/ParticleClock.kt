package com.shamilovstas.particleclock

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.shamilovstas.particleclock.background.ParticlesBackground
import com.shamilovstas.particleclock.geometry.AnalogClockGeometry
import com.shamilovstas.particleclock.hand.Hand
import com.shamilovstas.particleclock.model.angle.Angle
import com.shamilovstas.particleclock.model.angle.Sector
import com.shamilovstas.particleclock.model.point.CartesianPoint
import com.shamilovstas.particleclock.model.point.PolarPoint
import com.shamilovstas.particleclock.model.point.Radius
import com.shamilovstas.particleclock.model.time.Hour
import com.shamilovstas.particleclock.model.time.Minute
import com.shamilovstas.particleclock.model.time.Second
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

    private var isAnimationEnabled = true

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
        ParticlesBackground { this.invalidate() }


    // endregion
    // region Objects
    var secondsHandAngle = Angle()
    var minutesHand = Hand(radius = Radius(600f))
    var hoursHand = Hand(radius = Radius(400f))


    var clockRadius: Radius =
        Radius()
    var center: CartesianPoint = CartesianPoint()

    val analogClockGeometry = AnalogClockGeometry()

    object TemporaryHolders {
        fun refresh() {
            cartesianPoint.refresh()
            polarPoint.refresh()
        }

        var cartesianPoint = CartesianPoint()
        val polarPoint = PolarPoint()
        val sector = Sector()
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
        setMinuteHandAngle(minutes, seconds)
        setHourHandAngle(hours, minutes, seconds)
        setSecondHandAngle(seconds)
        val minuteHandAngleRange = minutesHand.sector.asRange()
        val hourHandAngleRange = hoursHand.sector.asRange()
        particlesBackground.allowedAngles = (0..360) - minuteHandAngleRange - hourHandAngleRange
        startPermanentAnimations()
        invalidate()
    }

    private fun drawMinutesIndicators(canvas: Canvas) {
        for (minute in 0 until MINUTES_IN_HOUR) {
            val angle =
                Angle((minute * DEGREE_PER_SEGMENT).toFloat())
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
        val trackCenter = TemporaryHolders.cartesianPoint.apply {
            x = 0f
            y = 0f
        }
        trackCenter.drawCircle(radius, canvas, secondsTrackPaint)

        if (secondsHandAngle.isInitialized()) {
            val sector = TemporaryHolders.sector
            sector.start = secondsHandAngle - indicatorAngle / 2f
            sector.end = indicatorAngle
            sector.drawSector(trackCenter, radius, canvas, secondsIndicatorPaint)
        }
    }

    private fun runSecondsAnimation(newAngle: Angle) {
        var angle = newAngle
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
                    if (isNewLap) secondsHandAngle = Angle(
                        360f
                    ) - secondsHandAngle
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

    private fun setSecondHandAngle(seconds: Int) {
        val newAngle = analogClockGeometry.secondsToAngle(Second(seconds))

        if (isAnimationEnabled) {
            runSecondsAnimation(newAngle)
        } else {
            secondsHandAngle = newAngle
        }
    }

    private fun startPermanentAnimations() {
        if (!isInitialized) {
            isInitialized = true
            if (isAnimationEnabled) {
                hoursHand.startAnimation { invalidate() }
                minutesHand.startAnimation { invalidate() }
                particlesBackground.startAnimation()
            }
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
