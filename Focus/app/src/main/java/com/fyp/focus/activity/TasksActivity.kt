package com.fyp.focus.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fyp.focus.R
import com.fyp.focus.adapter.TasksListAdapter
import com.fyp.focus.customclass.DBHelper
import com.fyp.focus.customclass.Task
import com.fyp.focus.dialog.CreateTaskDialogFragment
import com.fyp.focus.global.GlobalFunctions.logMessage
import com.fyp.focus.global.GlobalVariables
import com.fyp.focus.global.UserPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.collections.ArrayList

private const val TAG = "TasksActivity"

class TasksActivity: AppCompatActivity(), CreateTaskDialogFragment.CreateTaskDialogListener {

    private lateinit var rvTasksList: RecyclerView
    private lateinit var fabTasks: FloatingActionButton
    private lateinit var tvTasksList: TextView

    private lateinit var dbHelper: DBHelper
    private lateinit var preferences: UserPreferences

    private var tasksArray = ArrayList<Task>()

    private var tasksDatesStringArray = ArrayList<String>()
    private var tasksDatesFormattedStringArray = ArrayList<String>()
    private var tasksDatesArray = ArrayList<LocalDate>()
    private var tasksByDateArray =  ArrayList<Pair<String, ArrayList<Task>>>()

    private var tasksPriorityArray = ArrayList<String>()
    private var tasksByPriorityArray = ArrayList<Pair<String, ArrayList<Task>>>()

    private var tasksTypeArray = ArrayList<String>()
    private var tasksByTypeArray = ArrayList<Pair<String, ArrayList<Task>>>()

    private lateinit var adapter: TasksListAdapter

    private var currentFilterId = 0
    private var selectedFilter = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)
        title = "TASKS"
        tvTasksList = findViewById(R.id.tvTasksList)
        dbHelper = DBHelper(this, "focus", "tasks")
        preferences = UserPreferences(this)

        tasksPriorityArray = arrayListOf(getString(R.string.high_priority), getString(R.string.medium_priority), getString(R.string.low_priority))

        // check SharedPreferences for currently selected filter
        selectedFilter = when (preferences.selectedTaskFilter) {
            getString(R.string.filter_by_date) -> {
                currentFilterId = R.id.filterDate
                adapter = TasksListAdapter(this, dbHelper, tasksByDateArray)
                getString(R.string.filter_by_date)
            }
            getString(R.string.filter_by_priority) -> {
                currentFilterId = R.id.filterPriority
                adapter = TasksListAdapter(this, dbHelper, tasksByPriorityArray)
                getString(R.string.filter_by_priority)
            }
            getString(R.string.filter_by_type) -> {
                currentFilterId = R.id.filterType
                adapter = TasksListAdapter(this, dbHelper, tasksByTypeArray)
                getString(R.string.filter_by_type)
            }
            else -> {
                currentFilterId = R.id.filterDate
                adapter = TasksListAdapter(this, dbHelper, tasksByDateArray)
                getString(R.string.filter_by_date)
            }
        }
        logMessage(TAG, "selectedFilter: $selectedFilter")

        rvTasksList = findViewById(R.id.rvTasksList)
        rvTasksList.layoutManager = LinearLayoutManager(this)
        rvTasksList.adapter = adapter

        // check if DB table for tasks exists
        if (!preferences.tasksDbCreated) {
            logMessage(TAG, "table not created, creating now")
            dbHelper.createTaskTable(dbHelper.writableDatabase)
            preferences.tasksDbCreated = true
        }

        // check if DB table for tasks is empty, else fetch all tasks
        if (dbHelper.isTableEmpty(dbHelper.readableDatabase)) {
            logMessage(TAG, "db empty, making tv visible")
            tvTasksList.visibility = View.VISIBLE
        } else {
            logMessage(TAG, "db not empty, creating adapters")
            val cursor = dbHelper.readAllData(dbHelper.readableDatabase)
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    if (cursor.getString(2) !in tasksDatesStringArray) {
                        tasksDatesStringArray.add(cursor.getString(2))
                    }
                    val task = Task(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5).toBoolean()
                    )
                    tasksArray.add(task)
                    cursor.moveToNext()
                }
            }
            logMessage(TAG, "all tasks: $tasksArray")

            // sort array of unique dates and then format to look pretty
            val sortedArrays = sortDateArray(tasksDatesStringArray)
            tasksDatesArray = sortedArrays.first
            tasksDatesFormattedStringArray = sortedArrays.second

            // dateFilter array
            for (date in 0 until tasksDatesArray.size) {
                val tasks = ArrayList<Task>()
                val dateStr = tasksDatesArray[date].toString()
                for (task in tasksArray) {
                    if (task.date == dateStr) {
                        tasks.add(task)
                    }
                }
                tasksByDateArray.add(Pair(tasksDatesFormattedStringArray[date], tasks))
            }

            // priorityFilter array
            for (priority in tasksPriorityArray) {
                val tasks = ArrayList<Task>()
                for (task in tasksArray) {
                    if (task.priority == priority) {
                        tasks.add(task)
                    }
                }
                tasksByPriorityArray.add(Pair(priority, tasks))
            }

            // typeFilter array
            tasksTypeArray = ArrayList(GlobalVariables.taskTypes)
            tasksTypeArray.removeAt(0)
            for (type in tasksTypeArray) {
                val tasks = ArrayList<Task>()
                for (task in tasksArray) {
                    if (task.type == type) {
                        tasks.add(task)
                    }
                }
                tasksByTypeArray.add(Pair(type, tasks))
            }

            // create adapter with array corresponding to currently selected filter
            adapter = when (preferences.selectedTaskFilter) {
                getString(R.string.filter_by_date) -> TasksListAdapter(this, dbHelper, tasksByDateArray)
                getString(R.string.filter_by_priority) -> TasksListAdapter(this, dbHelper, tasksByPriorityArray)
                getString(R.string.filter_by_type) -> TasksListAdapter(this, dbHelper, tasksByTypeArray)
                else -> TasksListAdapter(this, dbHelper, tasksByDateArray)
            }
            rvTasksList.adapter = adapter
        }
        fabTasks = findViewById(R.id.fabTasks)
        fabTasks.setOnClickListener {
            val dialog = CreateTaskDialogFragment(dbHelper)
            dialog.show(this.supportFragmentManager, "CreateTaskDialog")
        }
    }

    /**
     * Sort a given ArrayList of dates in order with most recent date first
     *
     * @param dates the ArrayList of dates to be sorted
     * @returns Pair of ArrayLists, first list is sorted dates in LocalDate format for internal logic use,
     *          second list is sorted dates in String format for displaying to the user
     */
    private fun sortDateArray(dates: ArrayList<String>): Pair<ArrayList<LocalDate>, ArrayList<String>> {
        // create array of LocalDates and convert all dates to that format and add to array
        val dateArray = ArrayList<LocalDate>()
        for (item in dates) {
            val date = LocalDate.parse(item, DateTimeFormatter.ISO_DATE)
            dateArray.add(date)
        }
        // sort array of LocalDates
        dateArray.sort()
        //create array of Strings and convert all sorted dates to that format and add to array
        val sortedArray = ArrayList<String>()
        for (date in dateArray) {
            val formattedDate = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
            sortedArray.add(formattedDate)
        }
        return dateArray to sortedArray
    }

    /**
     * Find the index of a given date in the given ArrayList of sorted date
     *
     * @param date the date whose index we want to find
     * @param sortedDates the sorted ArrayList containing the date whose index we want to find
     * @return the index of the date
     */
    private fun findIndexOfDateInSortedArray(date: String, sortedDates: ArrayList<String>): Int {
        for (index in 0 until sortedDates.size) {
            if (date == sortedDates[index]) {
                return index
            }
        }
        return -1
    }

    /**
     * Handle the creation of a new Task
     *
     * @param dialog calling dialog
     * @param task the new Task
     */
    override fun onTaskCreated(dialog: DialogFragment, task: Task) {
        logMessage(TAG, "received task $task")
        dbHelper.insertTaskData(
            dbHelper.writableDatabase,
            task.name,
            task.type,
            task.date,
            task.time,
            task.priority,
            task.completed
        )

        // dateArray processing
        if (tasksByDateArray.isEmpty()) {
            tvTasksList.visibility = View.GONE
            val date = LocalDate.parse(task.date, DateTimeFormatter.ISO_DATE)
            tasksDatesArray.add(date)
            tasksDatesStringArray.add(date.toString())
            val dateStr = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
            tasksDatesFormattedStringArray.add(dateStr)
            tasksByDateArray.add(Pair(dateStr, arrayListOf(task)))
        } else if (tasksByDateArray.isNotEmpty()) {
            if (tasksDatesStringArray.contains(task.date)) {
                for (date in 0 until tasksDatesArray.size) {
                    if (task.date == tasksDatesArray[date].toString()) {
                        tasksByDateArray[date].second.add(task)
                    }
                }
            } else {
                val newDate = LocalDate.parse(task.date, DateTimeFormatter.ISO_DATE)
                tasksDatesArray.add(newDate)
                tasksDatesStringArray.add(newDate.toString())
                val newDateStr =
                    newDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                tasksDatesFormattedStringArray.add(task.date)
                val sortedArrays = sortDateArray(tasksDatesStringArray)
                tasksDatesArray = sortedArrays.first
                tasksDatesFormattedStringArray = sortedArrays.second
                val dateIndex =
                    findIndexOfDateInSortedArray(newDateStr, tasksDatesFormattedStringArray)
                tasksByDateArray.add(dateIndex, Pair(newDateStr, arrayListOf(task)))
            }
        }

        // priorityArray processing
        if (tasksByPriorityArray.isEmpty()) {
            tvTasksList.visibility = View.GONE
            for (priority in tasksPriorityArray) {
                tasksByPriorityArray.add(Pair(priority, arrayListOf()))
            }
        }
        for (priority in 0 until tasksPriorityArray.size) {
            if (task.priority == tasksPriorityArray[priority]) {
                logMessage(TAG, "adding ${task.name} to priority ${tasksPriorityArray[priority]}")
                tasksByPriorityArray[priority].second.add(task)
            }
        }
        logMessage(TAG, "tasksByPriorityArray: $tasksByPriorityArray")

        // typeArray processing
        if (tasksByTypeArray.isEmpty()) {
            tvTasksList.visibility = View.GONE
            tasksTypeArray = ArrayList(GlobalVariables.taskTypes)
            tasksTypeArray.removeAt(0)
            tasksByTypeArray.add(Pair(tasksTypeArray[0], arrayListOf(task)))
        } else if (tasksByTypeArray.isNotEmpty()) {
            if (tasksTypeArray.size != GlobalVariables.taskTypes.size-1) {
                logMessage(TAG, "types size has changed, current types: $tasksTypeArray")
                tasksTypeArray = ArrayList(GlobalVariables.taskTypes)
                tasksTypeArray.removeAt(0)
                logMessage(TAG, "new types array: $tasksTypeArray")
            }
            if (tasksTypeArray.last() == task.type && tasksTypeArray.size > 1) {
                tasksByTypeArray.add(Pair(task.type, arrayListOf(task)))
            } else {
                for (type in 0 until tasksTypeArray.size) {
                    if (task.type == tasksTypeArray[type]) {
                        logMessage(TAG, "adding ${task.name} to type ${tasksTypeArray[type]}")
                        tasksByTypeArray[type].second.add(task)
                    }
                }
            }
        }

        adapter = when (preferences.selectedTaskFilter) {
            getString(R.string.filter_by_date) -> TasksListAdapter(this, dbHelper, tasksByDateArray)
            getString(R.string.filter_by_priority) -> TasksListAdapter(this, dbHelper, tasksByPriorityArray)
            getString(R.string.filter_by_type) -> TasksListAdapter(this, dbHelper, tasksByTypeArray)
            else -> TasksListAdapter(this, dbHelper, tasksByDateArray)
        }
        rvTasksList.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_tasks_activity, menu)
        return true
    }

    /**
     * Handle changing task list filter
     *
     * @param item the selected menu item (filter)
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != currentFilterId)
            when (item.itemId) {
                R.id.filterDate -> {
                    currentFilterId = R.id.filterDate
                    selectedFilter = getString(R.string.filter_by_date)
                    preferences.selectedTaskFilter = getString(R.string.filter_by_date)
                    adapter = TasksListAdapter(this, dbHelper, tasksByDateArray)
                    rvTasksList.adapter = adapter
                    return true
                }
                R.id.filterPriority -> {
                    currentFilterId = R.id.filterPriority
                    selectedFilter = getString(R.string.filter_by_priority)
                    preferences.selectedTaskFilter = getString(R.string.filter_by_priority)
                    adapter = TasksListAdapter(this, dbHelper, tasksByPriorityArray)
                    rvTasksList.adapter = adapter
                    return true
                }
                R.id.filterType -> {
                    currentFilterId = R.id.filterType
                    selectedFilter = getString(R.string.filter_by_type)
                    preferences.selectedTaskFilter = getString(R.string.filter_by_type)
                    adapter = TasksListAdapter(this, dbHelper, tasksByTypeArray)
                    rvTasksList.adapter = adapter
                    return true
                }
            }
        return super.onOptionsItemSelected(item)
    }
}