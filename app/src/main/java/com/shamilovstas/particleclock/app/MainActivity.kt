package com.shamilovstas.particleclock.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.shamilovstas.particleclock.ParticleClock
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity() {
    private var currentTime = LocalTime.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val secondsSeekbar = findViewById<SeekBar>(R.id.seconds_seekbar)
        val minutesSeekbar = findViewById<SeekBar>(R.id.minutes_seekbar)
        val hoursSeekbar = findViewById<SeekBar>(R.id.hours_seekbar)

        val timeLabel = findViewById<TextView>(R.id.time)
        val autoMode = findViewById<CheckBox>(R.id.auto_mode)
        val clock = findViewById<ParticleClock>(R.id.clock)
        val formatter = DateTimeFormatter.ISO_LOCAL_TIME

         val seekbars = listOf(secondsSeekbar, minutesSeekbar, hoursSeekbar)
        autoMode.setOnCheckedChangeListener { _, isChecked -> seekbars.forEach { it.isEnabled = !isChecked  }}
        seekbars.forEach {
            it.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (autoMode.isChecked.not()) {
                        val minute = minutesSeekbar.progress
                        val second = secondsSeekbar.progress
                        val hour = hoursSeekbar.progress
                        val time = LocalTime.of(hour, minute, second)
                        timeLabel.text = formatter.format(time)
                        clock.setTime(time)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
        val handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null
        runnable = Runnable {
            if (autoMode.isChecked) {
                val minute = currentTime.minute
                val hour = currentTime.hour
                val second = currentTime.second
                secondsSeekbar.progress = second
                minutesSeekbar.progress = minute
                hoursSeekbar.progress = hour
                clock.setTime(currentTime)
                timeLabel.text = formatter.format(currentTime)
            }
            currentTime = currentTime.plus(1, ChronoUnit.SECONDS)
            handler.postDelayed(runnable!!, 1000)
        }
        handler.post(runnable)
    }
}