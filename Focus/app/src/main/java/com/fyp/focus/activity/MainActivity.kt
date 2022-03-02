package com.fyp.focus.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.fyp.focus.R

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var tvHeader: TextView
    private lateinit var btnTimers: Button
    private lateinit var btnTasks: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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