package com.shamilovstas.particleclock.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import com.shamilovstas.particleclock.ParticleClock
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val seekbar = findViewById<SeekBar>(R.id.time_seekbar)
        val timeLabel = findViewById<TextView>(R.id.time)
        val autoMode = findViewById<CheckBox>(R.id.auto_mode)
        val clock = findViewById<ParticleClock>(R.id.clock)
        val formatter = DateTimeFormatter.ISO_LOCAL_TIME

        autoMode.setOnCheckedChangeListener { _, isChecked -> seekbar.isEnabled = !isChecked }
        seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (autoMode.isChecked.not()) {
                    val time = LocalTime.ofSecondOfDay(progress.toLong())
                    timeLabel.text = formatter.format(time)
                    clock.setTime(time)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        var currentTime = LocalTime.now()
        val handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null
        runnable = Runnable {
            Log.d("Clock", currentTime.format(DateTimeFormatter.ISO_LOCAL_TIME))
            if (autoMode.isChecked) {
                val toSecondOfDay = currentTime.toSecondOfDay()
                Log.d("SecondOfDay", toSecondOfDay.toString())
                seekbar.progress = toSecondOfDay
                clock.setTime(currentTime)
                timeLabel.text = formatter.format(currentTime)
            }
            currentTime = currentTime.plus(1, ChronoUnit.SECONDS)
            handler.postDelayed(runnable!!, 1000)
        }
        handler.post(runnable)
    }
}