package com.fyp.focus.global

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    private val TASK_TYPE_ARRAY = "taskTypeArray"
    private val TASKS_DB_CREATED = "tasksDbCreated"
    private val SELECTED_TASK_FILTER = "selectedTaskFilter"
    private val DEFAULT_TIMER_DB_CREATED = "defaultTimerDbCreated"
    private val CUSTOM_TIMER_DB_CREATED = "customTimerDbCreated"

    var taskTypeArray: String?
        get() = preferences.getString(TASK_TYPE_ARRAY, "")
        set(value) = preferences.edit().putString(TASK_TYPE_ARRAY, value).apply()
    var tasksDbCreated: Boolean
        get() = preferences.getBoolean(TASKS_DB_CREATED, false)
        set(value) = preferences.edit().putBoolean(TASKS_DB_CREATED, value).apply()
    var selectedTaskFilter: String?
        get() = preferences.getString(SELECTED_TASK_FILTER, "")
        set(value) = preferences.edit().putString(SELECTED_TASK_FILTER, value).apply()

    var defaultTimerDbCreated: Boolean
        get() = preferences.getBoolean(DEFAULT_TIMER_DB_CREATED, false)
        set(value) = preferences.edit().putBoolean(DEFAULT_TIMER_DB_CREATED, value).apply()
    var customTimerDbCreated: Boolean
        get() = preferences.getBoolean(CUSTOM_TIMER_DB_CREATED, false)
        set(value) = preferences.edit().putBoolean(CUSTOM_TIMER_DB_CREATED, value).apply()
}