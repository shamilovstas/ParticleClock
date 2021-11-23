package com.shamilovstas.particleclock.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Clock(modifier: Modifier) {
    Box(modifier = modifier) {
        BasicText(text = "hellos  world")
    }
}

@Preview
@Composable
fun ClockPreview() {
    Clock(modifier = Modifier.fillMaxSize())
}