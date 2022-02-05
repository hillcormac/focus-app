package com.fyp.focus.activity

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.fyp.focus.R
import com.fyp.focus.global.GlobalFunctions.logError
import com.fyp.focus.global.GlobalFunctions.logMessage

private const val TAG = "TimerActivity"

class TimerActivity : AppCompatActivity() {

    private lateinit var timerName: String
    private var timeWork = 0
    private var timeShortBreak = 0
    private var timeLongBreak = 0
    private var intervals = 0

    private var currentInterval = 1
    private var currentPhase = "work"

    private lateinit var tvPhase: TextView
    private lateinit var tvIntervals: TextView
    private lateinit var btnControlTimer: Button
    private lateinit var btnPrevPhase: ImageButton
    private lateinit var btnNextPhase: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        timerName = intent.getStringExtra("timerName")!!
        timeWork = intent.getIntExtra("timeWork", 0)
        timeShortBreak = intent.getIntExtra("timeShortBreak", 0)
        timeLongBreak = intent.getIntExtra("timeLongBreak", 0)
        intervals = intent.getIntExtra("intervals", 0)

        logMessage(TAG, "timerDetails: $timerName, $timeWork, $timeShortBreak, $timeLongBreak, $intervals")

        initComponents()
    }

    private fun initComponents() {
        setTitle(timerName)
        tvPhase = findViewById(R.id.tvPhase)
        tvPhase.text = currentPhase
        tvIntervals = findViewById(R.id.tvIntervals)
        tvIntervals.text = "$currentInterval of $intervals"

        btnControlTimer = findViewById(R.id.btnControlTimer)
        btnControlTimer.setOnClickListener {
            btnControlTimer.text = when (btnControlTimer.text) {
                "start" -> "stop"
                "stop" -> "start"
                else -> "ERROR"
            }
        }
        btnPrevPhase = findViewById(R.id.btnPrevPhase)
        btnPrevPhase.setOnClickListener {
            when (currentPhase) {
                "work" -> {
                    currentInterval -= 1
                    tvIntervals.text = "$currentInterval of $intervals"
                    currentPhase = "break"
                    tvPhase.text = currentPhase
                    // Add timer state changes
                }
                "break" -> {
                    currentPhase = "work"
                    tvPhase.text = currentPhase
                    btnNextPhase.visibility = View.VISIBLE
                    if (currentInterval == 1) {
                        btnPrevPhase.visibility = View.GONE
                    }
                    // Add timer state changes
                }
            }
        }
        btnNextPhase = findViewById(R.id.btnNextPhase)
        btnNextPhase.setOnClickListener{
            when (currentPhase) {
                "break" -> {
                    currentInterval += 1
                    tvIntervals.text = "$currentInterval of $intervals"
                    currentPhase = "work"
                    tvPhase.text = currentPhase
                    // Add timer state changes
                }
                "work" -> {
                    currentPhase = "break"
                    tvPhase.text = currentPhase
                    btnPrevPhase.visibility = View.VISIBLE
                    if (currentInterval == intervals) {
                        btnNextPhase.visibility = View.GONE
                    }
                    // Add timer state changes
                }
            }
        }
    }
}