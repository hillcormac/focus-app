package com.fyp.focus.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import com.fyp.focus.R
import com.fyp.focus.global.GlobalFunctions.logMessage
import com.fyp.focus.global.GlobalVariables
import com.fyp.focus.global.UserPreferences

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var ivHeader: ImageView
    private lateinit var rlTimers: RelativeLayout
    private lateinit var rlTasks: RelativeLayout
    private lateinit var rlTips: RelativeLayout

    private lateinit var preferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // load taskTypes from preferences
        preferences = UserPreferences(this)
        val taskTypes = preferences.taskTypeArray?.split(",")
        if (taskTypes!!.size <= 1) {
            logMessage(TAG, "taskTypes is empty, adding default string")
            preferences.taskTypeArray = "${getString(R.string.select_task_type)},"
            GlobalVariables.taskTypes.add(getString(R.string.select_task_type))
        } else {
            for (index in 0 until taskTypes.size-1) {
                GlobalVariables.taskTypes.add(taskTypes[index])
            }
            logMessage(TAG, "taskTypes (${GlobalVariables.taskTypes.size}): ${GlobalVariables.taskTypes}")
        }

        ivHeader = findViewById(R.id.ivHeader)
        rlTimers = findViewById(R.id.rlTimers)
        rlTimers.setOnClickListener {
            val intent = Intent(this, TimerListActivity::class.java)
            startActivity(intent)
        }
        rlTasks = findViewById(R.id.rlTasks)
        rlTasks.setOnClickListener {
            val intent = Intent(this, TasksActivity::class.java)
            startActivity(intent)
        }
        rlTips = findViewById(R.id.rlTips)
        rlTips.setOnClickListener {
            val intent = Intent(this, TipsActivity::class.java)
            startActivity(intent)
        }
    }
}