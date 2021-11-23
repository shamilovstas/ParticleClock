package com.shamilovstas.particleclock.app

import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.shamilovstas.particleclock.view.ParticleClock
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
    private var currentTime = LocalTime.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.set_time).setOnClickListener { showTimePickerDialog() }

        val timeLabel = findViewById<TextView>(R.id.time)
        val clock = findViewById<ParticleClock>(R.id.clock)
        val formatter = DateTimeFormatter.ISO_LOCAL_TIME

        val handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null
        runnable = Runnable {
            Log.d("ParticleClock", currentTime.format(DateTimeFormatter.ISO_LOCAL_TIME))
            clock.setTime(currentTime)
            timeLabel.text = formatter.format(currentTime)
            currentTime = currentTime.plus(1, ChronoUnit.SECONDS)
            handler.postDelayed(runnable!!, 1000)
        }
        handler.post(runnable)
    }

    fun showTimePickerDialog() {
        TimePickerDialog(
            this,
            this,
            currentTime.hour,
            currentTime.minute,
            true
        ).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        currentTime = currentTime.withMinute(minute).withHour(hourOfDay)
    }
}