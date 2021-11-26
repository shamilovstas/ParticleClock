package com.shamilovstas.particleclock.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shamilovstas.particleclock.geometry.AnalogClockGeometry
import com.shamilovstas.particleclock.model.time.Minute

@Composable
fun Clock(modifier: Modifier) {
    val analogClockGeometry = remember { AnalogClockGeometry() }
    Canvas(modifier = modifier.padding(4.dp)) {
        Indicators(analogClockGeometry)
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
            val radius = this.size.width / 2f
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

@Preview()
@Composable
fun ClockPreview() {
    Clock(modifier = Modifier.fillMaxSize())
}