package com.fyp.focus.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import com.fyp.focus.R
import com.fyp.focus.global.GlobalFunctions.logMessage
import com.fyp.focus.global.GlobalVariables
import com.fyp.focus.global.UserPreferences

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var tvHeader: TextView
    private lateinit var btnTimers: Button
    private lateinit var btnTasks: Button

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

        tvHeader = findViewById(R.id.tvHeader)
        btnTimers = findViewById(R.id.btnTimers)
        btnTimers.setOnClickListener {
            val intent = Intent(this, TimerListActivity::class.java)
            startActivity(intent)
        }
        btnTasks = findViewById(R.id.btnTasks)
        btnTasks.setOnClickListener {
            val intent = Intent(this, TasksActivity::class.java)
            startActivity(intent)
        }
    }
}