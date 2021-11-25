package com.shamilovstas.particleclock.compose

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.Clock

fun Modifier.squareWidth() =
    this.then(layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        Log.d("CloseCompose", "Measured: ${placeable.width}x${placeable.height}")
        layout(placeable.width, placeable.width) {
            placeable.place(0, 0)
        }
    })

@Composable
fun Clock(modifier: Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        drawRect(
            Color.Blue,
        )
    }
}

@Preview()
@Composable
fun ClockPreview() {
    Clock(modifier = Modifier.fillMaxSize())
}