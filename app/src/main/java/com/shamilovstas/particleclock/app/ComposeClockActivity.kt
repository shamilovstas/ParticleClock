package com.shamilovstas.particleclock.app

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shamilovstas.particleclock.compose.Clock
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.isActive
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import java.time.LocalTime

class ComposeClockActivity: AppCompatActivity() {

    var currentTime = LocalTime.now()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val flow = flow<LocalTime> {
            while (currentCoroutineContext().isActive) {
                delay(1000)
                val newTime = currentTime.plusSeconds(1)
                emit(newTime)
                currentTime = newTime
            }
        }.onEach {
            Log.d("Clock", "Flow: $it")
        }
        setContent {
            val time by flow.collectAsState(initial = LocalTime.now())
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Clock(modifier = Modifier.size(300.dp), time = time)
            }
        }
    }
}