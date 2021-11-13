package com.shamilovstas.particleclock

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.shamilovstas.particleclock.hand.Hand
import java.time.LocalTime
import java.time.temporal.ChronoField
import kotlin.math.abs
import kotlin.math.min
import kotlin.properties.Delegates
import kotlin.random.Random

class ParticleClock @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : SurfaceView(context, attributeSet, defStyleAttr, defStyleRes), SurfaceHolder.Callback {

    private var drawingThread by Delegates.notNull<DrawingThread>()
    private val particlesHolder = ParticlesHolder()

    init {
        holder.addCallback(this)
    }

    private var color: Int = Color.WHITE

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
    var secondsHandAngle = Angle()
    var minutesHand = Hand(radius = Radius(600f))
    var hoursHand = Hand(radius = Radius(400f))

    var possibleAngleRange: List<Int> = listOf()

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
        particlesHolder.init(clockRadius)
    }
    // endregion

    override fun setBackgroundColor(color: Int) {
        this.color = color
    }

    private fun backgroundParticlesPulse(): Animator {
        val maxRadius = clockRadius - OUTER_SECONDS_TRACK_MARGIN
        val pulseAnimator = ValueAnimator.ofInt(0, 25)
        pulseAnimator.duration = 300

        pulseAnimator.interpolator = AccelerateDecelerateInterpolator()

        pulseAnimator.addUpdateListener {
            createParticlesMovementUpdater(maxRadius)
        }
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(pulseAnimator)
        return animatorSet
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawColor(color)
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

        minutesHand.draw(canvas, minutePaint)
        TemporaryHolders.refresh()
        hoursHand.draw(canvas, hourPaint)
        TemporaryHolders.refresh()
        // endregion

        particlesHolder.particles.forEach { it.draw(canvas, bubblePaint) }
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
        possibleAngleRange = (0..360) - minuteHandAngleRange - hourHandAngleRange
        invalidate()
    }

    private fun drawMinutesIndicators(canvas: Canvas) {
        for (minute in 0 until MINUTES_IN_HOUR) {
            val angle = Angle((minute * DEGREE_PER_SEGMENT).toFloat())
            TemporaryHolders.polarPoint.also {
                it.radius = clockRadius
                it.angle = angle
            }.run { toCartesian(TemporaryHolders.cartesianPoint) }
            val point = TemporaryHolders.cartesianPoint
            val isHour = analogClockGeometry.isSectorStart(Minute(minute))

            val radius = if (isHour) 20f else 10f
            val paint = if (isHour) hourPaint else minutePaint
            val indicator = TemporaryHolders.circle
            indicator.center.copyFrom(point)
            indicator.radius = Radius(radius)
            indicator.draw(canvas, paint)
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
            animator.playTogether(secondsAnimation, backgroundParticlesPulse())
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

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawingThread = DrawingThread(getHolder(), this)
        drawingThread.start()
        hoursHand.startAnimation { invalidate() }
        minutesHand.startAnimation { invalidate() }
        startLinearBackgroundParticles()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        drawingThread.interrupt()
    }

    // This method might be the source of the occasional native crashes
    private fun createParticlesMovementUpdater(
        maxRadius: Radius,
    ) {
        for (bubble in particlesHolder.particles) {

            val point = bubble.point
            val radius = point.radius
            val nextRadius = radius + 1f
            point.radius = if (nextRadius > maxRadius) {
                val angle = possibleAngleRange.random() + Random.nextFloat(-1f, +1f)
                point.angle = Angle(abs(angle) % 360)
                Radius(BUBBLE_SPAWN_CENTER_MARGIN)
            } else nextRadius

            val sizeMultiplier = particlesHolder.getDistancePercent(
                point.radius - BUBBLE_SPAWN_CENTER_MARGIN,
                maxRadius - BUBBLE_SPAWN_CENTER_MARGIN
            )
            bubble.setRadiusMultiplier(sizeMultiplier)
        }
        invalidate()
    }

    private fun startLinearBackgroundParticles() {
        // speed: 100px per second
        val animator = infiniteAnimator(100, LinearInterpolator()) {
            createParticlesMovementUpdater(clockRadius - OUTER_SECONDS_TRACK_MARGIN)
        }
        animator.start()
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
