package com.fyp.focus.activity

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.TypedValue
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

    private var lightMode = false
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        // initialise layout and components
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        val outValue = TypedValue()
        theme.resolveAttribute(R.attr.themeName, outValue, true)
        if (outValue.string == "light") {
            lightMode = true
            supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.pale_red)))
        }
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // get extras and create Timer instance
        val timerName = intent.getStringExtra("timerName")!!
        val timeWork = intent.getStringExtra("timeWork")!!
        val timeShortBreak = intent.getStringExtra("timeShortBreak")!!
        val timeLongBreak = intent.getStringExtra("timeLongBreak")!!
        val intervals = intent.getStringExtra("intervals")!!.toInt()

        timer = Timer(timerName, timeWork, timeShortBreak, timeLongBreak, intervals)

        logMessage(TAG, "timer: $timer")
        initComponents()
    }

    /**
     * Cancel any active CountDownTimer when the Activity is destroyed
     */
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    /**
     * Initialise the components of the Activity
     */
    private fun initComponents() {
        title = timer.name
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

    /**
     * Control the timer i.e. start, stop, reset
     */
    private fun controlTimer() {
        when (isTimerRunning) {
            false -> {
                // timer is not running, check if it was already started this phase
                countDownTimer = if (!isPhaseStarted) {
                    // if not started this phase run from beginning
                    logMessage(TAG, "starting timer")
                    when (tvPhase.text) {
                        "work" -> createCountDownTimer(timer.timeWorkMillis, timer.timeWorkMins, timer.timeWorkSecs).start()
                        "short break" -> createCountDownTimer(timer.timeShortBreakMillis, timer.timeShortBreak, 0).start()
                        "long break" -> createCountDownTimer(timer.timeLongBreakMillis, timer.timeLongBreak, 0).start()
                        else -> null
                    }
                } else {
                    // if already started, start running from paused details
                    logMessage(TAG, "resuming timer")
                    createCountDownTimer(pausedTimerDetails[0].toLong(), pausedTimerDetails[1], pausedTimerDetails[2], true).start()
                }
                isTimerRunning = true
                isPhaseStarted = true
                btnControlTimer.text = "stop"
            }
            true -> {
                // stop timer if running and save remaining time to variable
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
            // phase complete, move to next phase of timer
            currentInterval = 1
            nextPhase()
        }
    }

    /**
     * Create an instance of CountDownTimer
     *
     * @param totalTime the totalTime of the CountDownTimer
     * @param totalTimeMins the minutes in the starting totalTime (for display purposes)
     * @param totalTimeSecs the seconds in the starting totalTime (for display purposes)
     * @param resumedTimer true if this CountDownTimer is starting from a previously paused instance
     * @return a CountDownTimer instance
     */
    private fun createCountDownTimer(totalTime: Long, totalTimeMins: Int, totalTimeSecs: Int, resumedTimer: Boolean = false): CountDownTimer {

        // override the total time if this instance is not resuming a paused timer
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
                logMessage(TAG, "tick = ${tvTimer.text}, progess = ${pbTimer.progress}")
            }

            override fun onFinish() {
                // vibrate device when timer ends, and move to next phase or allow timer to be reset if finished
                vibrator.vibrate(600)
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

    /**
     * Move timer to next phase
     */
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

    /**
     * Move timer to previous phase
     */
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

    /**
     * Update timer visuals and variables when changing phase
     */
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

    /**
     * Set colours of the view
     *
     * @param colour the colour to set the elements of the view
     */
    private fun setColours(colour: Int) {
        pbTimer.progressTintList = ColorStateList.valueOf(colour)
        btnControlTimer.setBackgroundColor(colour)
        btnNextPhase.imageTintList = ColorStateList.valueOf(colour)
        btnPrevPhase.imageTintList = ColorStateList.valueOf(colour)
        if (lightMode) {
            logMessage(TAG, "setting title colour = $colour")
            supportActionBar?.setBackgroundDrawable(ColorDrawable(colour))
        }
    }
}