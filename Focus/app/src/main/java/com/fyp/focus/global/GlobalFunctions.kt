package com.fyp.focus.global

import android.util.Log

private const val TAG = "GlobalFunctions"

object GlobalFunctions {

    fun logMessage(tag: String, message: String) {
        if (GlobalVariables.loggingEnabled) {
            Log.d(tag, message)
        }
    }

    fun logError(tag: String, message: String) {
        if (GlobalVariables.loggingEnabled) {
            Log.e(tag, message)
        }
    }
}