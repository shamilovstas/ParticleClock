package com.shamilovstas.particleclock.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.dp
import com.shamilovstas.particleclock.geometry.AnalogClockGeometry
import com.shamilovstas.particleclock.model.time.Minute

@Composable
fun Clock(modifier: Modifier) {
    val analogClockGeometry = remember { AnalogClockGeometry() }
    Canvas(modifier = modifier.padding(4.dp)) {
        Indicators(analogClockGeometry)
        LargeSecondsIndicator()
        SmallSecondsIndicator()
    }
}

fun DrawScope.Indicators(analogClockGeometry: AnalogClockGeometry) {
    repeat(60) {
        val minute = Minute(it)
        val isHourIndicator = analogClockGeometry.isSectorStart(minute)
        val angle = minute.value * (360 / 60)
        val size = if (isHourIndicator) 4.dp else 2.5.dp
        val style = if (isHourIndicator) Fill else Stroke(0.25.dp.toPx())
        rotate(degrees = angle.toFloat()) {
            translate(left = radius) {
                drawCircle(
                    color = Color.Blue,
                    radius = size.toPx(),
                    style = style,
                )
            }
        }
    }
}

fun DrawScope.SmallSecondsIndicator() {
    val radius = 25.dp.toPx()
    SecondsCircle(radius = radius)
    SecondsIndicator(angle = 90f, sweepAngle = 30f, radius = radius)
}

fun DrawScope.LargeSecondsIndicator() {
    val radius = radius - 12.dp.toPx()
    SecondsCircle(radius = radius)
    SecondsIndicator(angle = 90f, sweepAngle = 10f, radius = radius)
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