package com.shamilovstas.particleclock.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.shamilovstas.particleclock.ParticleClock
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val clock = findViewById<ParticleClock>(R.id.clock)
        var currentTime = LocalTime.now()
        val handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null
        runnable = Runnable {
            Log.d("Clock", currentTime.format(DateTimeFormatter.ISO_LOCAL_TIME))
            clock.setTime(currentTime)
            currentTime = currentTime.plus(1, ChronoUnit.SECONDS)
            handler.postDelayed(runnable!!, 1000)
        }
        handler.post(runnable)
    }
}