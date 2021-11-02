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
import java.time.LocalTime
import java.time.temporal.ChronoField
import kotlin.math.min
import kotlin.math.roundToInt
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
    var secondsHandAngle = Float.NaN
    var minutesHandAngle = Float.NaN
    var hoursHandAngle = Float.NaN

    var possibleAngleRange: List<Int> = listOf()


    var clockCircle = Circle()

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
        clockCircle = Circle(CartesianPoint(cx, cy), min(cx, cy) - 40f)
        particlesHolder.init(clockCircle.radius)
    }
    // endregion

    override fun setBackgroundColor(color: Int) {
        this.color = color
    }


    private fun pulse(): Animator {
        val maxRadius = clockCircle.radius - OUTER_SECONDS_TRACK_MARGIN
        val pulseAnimator = ValueAnimator.ofInt(0, 25)
        val linearAnimator = ValueAnimator.ofInt(0, 70)
        linearAnimator.duration = 1000
        pulseAnimator.duration = 300

        linearAnimator.interpolator = LinearInterpolator()
        pulseAnimator.interpolator = AccelerateDecelerateInterpolator()

        pulseAnimator.addUpdateListener(
            createParticlesMovementUpdater(
                maxRadius,
                MovementType.PULSE
            )
        )
        linearAnimator.addUpdateListener(
            createParticlesMovementUpdater(
                maxRadius,
                MovementType.LINEAR
            )
        )
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(linearAnimator, pulseAnimator)
        return animatorSet
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawColor(color);
        canvas.translate(clockCircle.x.toFloat(), clockCircle.y.toFloat())
        val radius = clockCircle.radius
        // region 1. Drawing outer clock contour (minutes and hours indicators)
        drawMinutesIndicators(canvas)
        TemporaryHolders.refresh()
        // endregion
        // region 2. Drawing the seconds track (may be referred as the 'inner circle')
        drawSecondsTrack(
            canvas,
            radius - OUTER_SECONDS_TRACK_MARGIN,
            OUTER_SECONDS_INDICATOR_SWEEP_ANGLE
        )
        TemporaryHolders.refresh()
        drawSecondsTrack(canvas, INNER_SECONDS_TRACK_MARGIN, INNER_SECONDS_INDICATOR_SWEEP_ANGLE)
        TemporaryHolders.refresh()

        drawDebugMinuteHand(canvas)
        TemporaryHolders.refresh()
        drawDebugHourHand(canvas)
        TemporaryHolders.refresh()
        // endregion

        particlesHolder.bubbles.forEach { it.draw(canvas, bubblePaint) }
    }

    private fun drawDebugHourHand(canvas: Canvas) {
        if (!hoursHandAngle.isNaN()) {
            val circle = TemporaryHolders.circle.apply {
                radius = 400f
                this.center.copyFrom(clockCircle.center)
            }
            circle.getPoint(hoursHandAngle, TemporaryHolders.cartesianPoint)
            val end = TemporaryHolders.cartesianPoint
            canvas.drawLine(
                0f, 0f,
                end.x,
                end.y,
                hourPaint
            )
        }
    }

    private fun drawDebugMinuteHand(canvas: Canvas) {
        if (!minutesHandAngle.isNaN()) {

            clockCircle.getPoint(minutesHandAngle, TemporaryHolders.cartesianPoint)
            val end = TemporaryHolders.cartesianPoint
            canvas.drawLine(
                0f, 0f,
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

        val minuteHandAngleRange =
            ((minutesHandAngle.roundToInt() - 7)..(minutesHandAngle.roundToInt() + 7))
        val hourHandAngleRange =
            ((hoursHandAngle.roundToInt() - 7)..(hoursHandAngle.roundToInt() + 7))
        possibleAngleRange = (0..360) - minuteHandAngleRange - hourHandAngleRange
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

    var isDryRun = false
    var hasRun = false
    private fun runSecondsTrackAnimation(seconds: Int) {

        var angle = analogClockGeometry.secondsToAngle(Second(seconds))
        if (secondsHandAngle.isNaN()) {
            secondsHandAngle = angle
        } else {
            if (isDryRun && hasRun) return
            hasRun = true
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

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawingThread = DrawingThread(getHolder(), this)
        drawingThread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        drawingThread.interrupt()
    }

    private fun createParticlesMovementUpdater(
        maxRadius: Float,
        kind: MovementType
    ): ValueAnimator.AnimatorUpdateListener {
        return object : ValueAnimator.AnimatorUpdateListener {
            var previous = 0
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val animatedValue = animation.animatedValue as Int
                val value = animatedValue - previous
                previous = animatedValue
                for (bubble in particlesHolder.bubbles) {

                    if (kind == MovementType.LINEAR && bubble.autoMove) {
                        continue
                    }
                    val point = bubble.point
                    val radius = point.radius
                    var newRadius = radius + value

                    if (newRadius > maxRadius) {
                        newRadius = BUBBLE_SPAWN_CENTER_MARGIN
                        point.angle = possibleAngleRange.random() + Random.nextFloat(-1f, +1f)
                    }
                    point.radius = newRadius

                    val sizeMultiplier = particlesHolder.getDistancePercent(
                        newRadius - BUBBLE_SPAWN_CENTER_MARGIN,
                        maxRadius - BUBBLE_SPAWN_CENTER_MARGIN
                    )
                    bubble.setRadiusMultiplier(sizeMultiplier)
                }
                invalidate()
            }
        }
    }

    enum class MovementType {
        PULSE, LINEAR
    }

    companion object {
        const val MIN_SIZE = 1000
        const val MINUTES_IN_HOUR = 60
        const val DEGREE_PER_SEGMENT = 360 / 60
        const val OUTER_SECONDS_TRACK_MARGIN = 50
        const val INNER_SECONDS_TRACK_MARGIN = 80f
        const val BUBBLE_SPAWN_CENTER_MARGIN = INNER_SECONDS_TRACK_MARGIN
        const val OUTER_SECONDS_INDICATOR_SWEEP_ANGLE = 4f
        const val INNER_SECONDS_INDICATOR_SWEEP_ANGLE = 40f
    }
}
