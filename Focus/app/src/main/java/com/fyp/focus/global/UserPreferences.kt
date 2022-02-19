package com.fyp.focus.global

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    private val DEFAULT_TIMER_DB_CREATED = "defaultTimerDbCreated"
    private val CUSTOM_TIMER_DB_CREATED = "customTimerDbCreated"

    var defaultTimerDbCreated: Boolean
        get() = preferences.getBoolean(DEFAULT_TIMER_DB_CREATED, false)
        set(value) = preferences.edit().putBoolean(DEFAULT_TIMER_DB_CREATED, value).apply()
    var customTimerDbCreated: Boolean
        get() = preferences.getBoolean(CUSTOM_TIMER_DB_CREATED, false)
        set(value) = preferences.edit().putBoolean(CUSTOM_TIMER_DB_CREATED, value).apply()
}