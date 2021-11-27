package com.shamilovstas.particleclock.compose

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.dp
import com.shamilovstas.particleclock.geometry.AnalogClockGeometry
import com.shamilovstas.particleclock.model.angle.Angle
import com.shamilovstas.particleclock.model.point.PolarPoint
import com.shamilovstas.particleclock.model.point.Radius
import com.shamilovstas.particleclock.model.time.Minute
import com.shamilovstas.particleclock.model.time.Second
import com.shamilovstas.particleclock.util.nextFloat
import java.time.LocalTime
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Clock(modifier: Modifier, time: LocalTime = LocalTime.now()) {
    Log.d("ClockCompose", "Recomposed: $time")
    val second = Second(time.second)
    val analogClockGeometry = remember { AnalogClockGeometry() }
    val secondAngleAnimatable =
        remember { Animatable(analogClockGeometry.secondsToAngle(second = second).angle) }
    Canvas(modifier = modifier.padding(4.dp)) {
        Log.d("ClockCompose", "Canvas")

        Indicators(analogClockGeometry)

        val smallRadius = 25.dp.toPx()
        val largeRadius = radius - 12.dp.toPx()

        SecondsCircle(radius = smallRadius)

        SecondsCircle(radius = largeRadius)

        SecondsIndicator(
            angle = secondAngleAnimatable.value,
            sweepAngle = 10f,
            radius = largeRadius
        )
        SecondsIndicator(
            angle = secondAngleAnimatable.value,
            sweepAngle = 30f,
            radius = smallRadius
        )
    }

    RunSecondTrackAnimation(
        key = second,
        oldAngle = secondAngleAnimatable.value,
        newAngle = analogClockGeometry.secondsToAngle(second).angle,
        animateCallback = { secondAngleAnimatable.animateTo(it) },
        snapCallback = { secondAngleAnimatable.snapTo(it) }
    )
}

fun generateRandomParticles(
    count: Int = 1000,
    minRadius: Float,
    maxRadius: Float
): List<PolarPoint> = List(count) {
    val randomAngle = Random.nextInt(0, 360).toFloat()
    val radius = Random.nextFloat(minRadius, maxRadius)
    PolarPoint(Radius(radius), Angle(randomAngle))
}

@Composable
fun RunSecondTrackAnimation(
    key: Second,
    newAngle: Float,
    oldAngle: Float,
    animateCallback: suspend (Float) -> Unit,
    snapCallback: suspend (Float) -> Unit
) {
    LaunchedEffect(key1 = key) {
        var isNewLap = false
        var targetAngle = newAngle

        if (targetAngle - oldAngle < 0f) {
            isNewLap = true
            targetAngle += 360f
        }
        animateCallback(targetAngle)
        if (isNewLap) {
            snapCallback(targetAngle - 360f)
        }
    }
}

@Composable
fun Particle(
    modifier: Modifier = Modifier,
    color: Color = Color.Blue,
    center: Offset,
    style: DrawStyle,
    radius: Float
) {
    Canvas(modifier = modifier) {
        drawCircle(
            color = color,
            style = style,
            radius = radius,
            center = center
        )
    }
}

fun DrawScope.Indicators(analogClockGeometry: AnalogClockGeometry) {
    repeat(60) {
        val minute = Minute(it)
        val isHourIndicator = analogClockGeometry.isSectorStart(minute)
        val angle = minute.value * (360 / 60)
        val size = if (isHourIndicator) 4.dp else 2.5.dp
        val style = if (isHourIndicator) Fill else Stroke(0.25.dp.toPx())
        withTransform(
            transformBlock = {
                rotate(degrees = angle.toFloat())
                translate(left = this@Indicators.radius)
            }
        ) {
            drawCircle(
                color = Color.Blue,
                radius = size.toPx(),
                style = style,
            )
        }
    }
}

fun DrawScope.SecondsIndicator(
    color: Color = Color.Blue,
    angle: Float,
    sweepAngle: Float,
    radius: Float
) {
    val offset = Offset(this.center.x - radius, this.center.y - radius)
    val size = Size(radius * 2, radius * 2)
    val startAngle = angle - sweepAngle / 2
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        false,
        style = Stroke(1.75.dp.toPx(), cap = StrokeCap.Round),
        topLeft = offset,
        size = size
    )
}

fun DrawScope.SecondsCircle(color: Color = Color.Blue, radius: Float) {
    drawCircle(
        color = color,
        radius = radius,
        style = Stroke(0.25.dp.toPx())
    )
}

val DrawScope.radius: Float get() = this.size.width / 2f

data class Particle(var x: Float, var y: Float)