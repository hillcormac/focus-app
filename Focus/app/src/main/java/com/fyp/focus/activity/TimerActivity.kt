package com.fyp.focus.activity

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.fyp.focus.R
import com.fyp.focus.customclass.Timer
import com.fyp.focus.global.GlobalFunctions.logMessage

private const val TAG = "TimerActivity"

class TimerActivity : AppCompatActivity() {

    private lateinit var timer: Timer
    private var countDownTimer: CountDownTimer? = null

    private var currentInterval = 1
    private var currentPhase = "work"
    private var isTimerRunning = false
    private var isPhaseStarted = false
    private var currentTimerTotalMillis: Long = 0
    private var pausedTimerDetails = emptyArray<Int>()

    private lateinit var tvPhase: TextView
    private lateinit var tvIntervals: TextView
    private lateinit var tvTimer: TextView
    private lateinit var pbTimer: ProgressBar
    private lateinit var btnControlTimer: Button
    private lateinit var btnPrevPhase: ImageButton
    private lateinit var btnNextPhase: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        val timerName = intent.getStringExtra("timerName")!!
        val timeWork = intent.getStringExtra("timeWork")!!
        val timeShortBreak = intent.getStringExtra("timeShortBreak")!!
        val timeLongBreak = intent.getStringExtra("timeLongBreak")!!
        val intervals = intent.getStringExtra("intervals")!!.toInt()

        timer = Timer(timerName, timeWork, timeShortBreak, timeLongBreak, intervals)

        logMessage(TAG, "timer: $timer")
        initComponents()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    private fun initComponents() {
        setTitle(timer.name)
        tvPhase = findViewById(R.id.tvPhase)
        tvPhase.text = currentPhase
        tvIntervals = findViewById(R.id.tvIntervals)
        tvIntervals.text = "$currentInterval of ${timer.intervals}"
        tvTimer = findViewById(R.id.tvTimer)
        if (timer.timeWorkSecs == 0) {
            tvTimer.text = "${timer.timeWorkMins}:${timer.timeWorkSecs}0"
        } else {
            tvTimer.text = "${timer.timeWorkMins}:${timer.timeWorkSecs}"
        }
        pbTimer = findViewById(R.id.pbTimer)

        btnControlTimer = findViewById(R.id.btnControlTimer)
        btnControlTimer.setOnClickListener {
            controlTimer()
        }
        btnPrevPhase = findViewById(R.id.btnPrevPhase)
        btnPrevPhase.setOnClickListener {
            prevPhase()
        }
        btnNextPhase = findViewById(R.id.btnNextPhase)
        btnNextPhase.setOnClickListener{
            nextPhase()
        }
    }

    private fun controlTimer() {
        when (isTimerRunning) {
            false -> {
                countDownTimer = if (!isPhaseStarted) {
                    logMessage(TAG, "starting timer")
                    when (tvPhase.text) {
                        "work" -> createCountDownTimer(timer.timeWorkMillis, timer.timeWorkMins, timer.timeWorkSecs).start()
                        "short break" -> createCountDownTimer(timer.timeShortBreakMillis, timer.timeShortBreak, 0).start()
                        "long break" -> createCountDownTimer(timer.timeLongBreakMillis, timer.timeLongBreak, 0).start()
                        else -> null
                    }
                } else {
                    logMessage(TAG, "resuming timer")
                    createCountDownTimer(pausedTimerDetails[0].toLong(), pausedTimerDetails[1], pausedTimerDetails[2], true).start()
                }
                isTimerRunning = true
                isPhaseStarted = true
                btnControlTimer.text = "stop"
            }
            true -> {
                logMessage(TAG, "pausing timer")
                countDownTimer?.cancel()
                isTimerRunning = false
                btnControlTimer.text = "start"

                val splitTimeRemaining = tvTimer.text.split(":")
                val remainingMins = splitTimeRemaining[0].toInt()
                val remainingSecs = splitTimeRemaining[1].toInt()
                var remainingMillis = (((remainingMins * 60) + remainingSecs) * 1000).toLong()
                pausedTimerDetails = arrayOf(remainingMillis.toInt(), remainingMins, remainingSecs)
            }
        }
        if (currentPhase == "complete") {
            currentInterval = 1
            nextPhase()
        }
    }

    private fun createCountDownTimer(totalTime: Long, totalTimeMins: Int, totalTimeSecs: Int, resumedTimer: Boolean = false): CountDownTimer {

        if (!resumedTimer) {
            currentTimerTotalMillis = totalTime
        }

        return object: CountDownTimer(totalTime, 1000) {

            var timeMins = totalTimeMins
            var timeSecs = totalTimeSecs

            override fun onTick(millisUntilFinished: Long) {
                timeSecs--
                tvTimer.text = when {
                    timeSecs < 0 -> {
                        timeMins--
                        timeSecs = 59
                        "$timeMins:$timeSecs"
                    }
                    timeSecs < 10 -> {
                        "$timeMins:0$timeSecs"
                    }
                    else -> {
                        "$timeMins:$timeSecs"
                    }
                }
                val remainingPercentage = (millisUntilFinished.toDouble()/currentTimerTotalMillis.toDouble()) * pbTimer.max
                pbTimer.progress = remainingPercentage.toInt()
//                logMessage(TAG, "tick = ${tvTimer.text}, progess = ${pbTimer.progress}")
            }

            override fun onFinish() {
                logMessage(TAG, "timer finished")
                isTimerRunning = false
                pbTimer.progress = 0
                if (currentPhase == "long break") {
                    btnControlTimer.text = "reset"
                    currentPhase = "complete"
                } else {
                    nextPhase()
                }
            }
        }
    }

    private fun nextPhase() {

        if (currentPhase.contains("break")) {
            currentInterval++
            tvIntervals.text = "$currentInterval of ${timer.intervals}"
            currentPhase = "work"
            tvPhase.text = currentPhase
        } else if (currentPhase.contains("work")) {
            currentPhase = when (currentInterval) {
                timer.intervals -> "long break"
                else -> "short break"
            }
            tvPhase.text = currentPhase
            btnPrevPhase.visibility = View.VISIBLE
            if (currentInterval == timer.intervals) {
                btnNextPhase.visibility = View.GONE
            }
        } else if (currentPhase.contains("complete")) {
            tvIntervals.text = "$currentInterval of ${timer.intervals}"
            currentPhase = "work"
            tvPhase.text = currentPhase
            btnPrevPhase.visibility = View.GONE
            btnNextPhase.visibility = View.VISIBLE
        }

        changeTimerPhase()
    }

    private fun prevPhase() {

        if (currentPhase.contains("work")) {
            currentInterval--
            tvIntervals.text = "$currentInterval of ${timer.intervals}"
            currentPhase = when (currentInterval) {
                timer.intervals -> "long break"
                else -> "short break"
            }
            tvPhase.text = currentPhase
        } else if (currentPhase.contains("break")) {
            currentPhase = "work"
            tvPhase.text = currentPhase
            btnNextPhase.visibility = View.VISIBLE
            if (currentInterval == 1) {
                btnPrevPhase.visibility = View.GONE
            }
        }

        changeTimerPhase()
    }

    private fun changeTimerPhase() {
        if (isTimerRunning) {
            countDownTimer?.cancel()
            isTimerRunning = false
        }
        isPhaseStarted = false
        btnControlTimer.text = "start"
        pbTimer.progress = pbTimer.max
        tvTimer.text = when (currentPhase) {
            "work" -> {
                setColours(resources.getColor(R.color.pale_red))
                if (timer.timeWorkSecs < 10) {
                    "${timer.timeWorkMins}:0${timer.timeWorkSecs}"
                } else {
                    "${timer.timeWorkMins}:${timer.timeWorkSecs}"
                }
            }
            "short break" -> {
                setColours(resources.getColor(R.color.pale_blue))
                "${timer.timeShortBreak}:00"
            }
            "long break" -> {
                setColours(resources.getColor(R.color.dark_blue))
                "${timer.timeLongBreak}:00"
            }
            else -> "0:00"
        }
    }

    private fun setColours(colour: Int) {
        pbTimer.progressTintList = ColorStateList.valueOf(colour)
        btnControlTimer.setBackgroundColor(colour)
        btnNextPhase.imageTintList = ColorStateList.valueOf(colour)
        btnPrevPhase.imageTintList = ColorStateList.valueOf(colour)
    }
}