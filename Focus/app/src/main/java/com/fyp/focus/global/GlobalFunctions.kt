package com.fyp.focus.global

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.fyp.focus.R

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

    fun toastMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}