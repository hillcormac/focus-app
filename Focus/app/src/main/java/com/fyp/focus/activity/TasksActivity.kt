package com.fyp.focus.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fyp.focus.R
import com.fyp.focus.adapter.TasksListAdapter
import com.fyp.focus.customclass.Task
import com.fyp.focus.global.GlobalFunctions.toastMessage
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val TAG = "TasksActivity"

class TasksActivity: AppCompatActivity() {

    private lateinit var rvTasksList: RecyclerView
    private lateinit var fabTasks: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)
        title = "TASKS"

        val task1 = Task("test task", "monday", "work", "HIGH")
        val task2 = Task("other task", "monday", "work", "MEDIUM")
        val task3 = Task("do things", "tuesday", "personal", "LOW")
        val task4 = Task("deadline", "wednesday", "work", "HIGH")
        val task5 = Task("experiment", "wednesday", "work", "MEDIUM")
        val task6 = Task("thingy ma bob", "thursday", "work", "MEDIUM")
        val task7 = Task("stuff", "thursday", "work", "MEDIUM")
        val task8 = Task("thangs", "thursday", "work", "MEDIUM")
        val task9 = Task("oh ya that thing", "friday", "work", "MEDIUM")
        val task10 = Task("testerooni", "saturday", "work", "MEDIUM")

        val items = ArrayList<Pair<String, Array<Task>>>()
        items.add(Pair("monday", arrayOf(task1, task2)))
        items.add(Pair("tuesday", arrayOf(task3)))
        items.add(Pair("wednesday", arrayOf(task4, task5)))
        items.add(Pair("thursday", arrayOf(task6, task7, task8)))
        items.add(Pair("friday", arrayOf(task9)))
        items.add(Pair("saturday", arrayOf(task10)))

        rvTasksList = findViewById(R.id.rvTasksList)
        rvTasksList.layoutManager = LinearLayoutManager(this)
        rvTasksList.adapter = TasksListAdapter(this, items)
        fabTasks = findViewById(R.id.fabTasks)
        fabTasks.setOnClickListener {
            toastMessage(this, "New Task Dialog Goes Here")
        }

        /**
         * Dynamically sized lists as children of recyclerview items
         * Nested recyclerview
         * Pass array of Pair<String, Array<Task>> to first recyclerview
         * Pass Array<Task> to the nested recyclerview per item
         */
    }
}