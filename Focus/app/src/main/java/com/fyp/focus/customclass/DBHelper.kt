package com.fyp.focus.customclass

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import com.fyp.focus.global.GlobalFunctions.logMessage

private const val TAG = "DBHelper"

/**
 * Extension of SQLiteOpenHelper to provide additional custom functionality and queries
 *
 * @param context calling context
 * @param dbName name of the database
 * @param table name of the table an instance is created to query
 */
class DBHelper(
    context: Context,
    dbName: String = "focus",
    private val table: String
): SQLiteOpenHelper(context, dbName, null, 1) {

    /**
     * Create the instance table if it does not exist when the instance is created
     */
    override fun onCreate(db: SQLiteDatabase?) {
        if (table.contains("timers")) {
            db?.execSQL("CREATE TABLE IF NOT EXISTS $table(name TEXT primary key, time_work TEXT, time_short_break TEXT, time_long_break TEXT, intervals INTEGER);")
        } else if (table.contains("tasks")) {
            db?.execSQL("CREATE TABLE IF NOT EXISTS $table(name TEXT primary key, type TEXT, date TEXT, time TEXT, priority TEXT, completed TEXT);")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (table.contains("timers")) {
            db?.execSQL("DROP TABLE IF EXISTS $table(name TEXT primary key, time_work TEXT, time_short_break TEXT, time_long_break TEXT, intervals INTEGER);")
        } else if (table.contains("tasks")) {
            db?.execSQL("DROP TABLE IF EXISTS $table(name TEXT primary key, type TEXT, date TEXT, time TEXT, priority TEXT, completed TEXT);")
        }
    }

    /**
     * Insert default timer data
     */
    private fun insertDefaultTimerData(db: SQLiteDatabase) {
        var result = insertTimerData(db, "pomodoro", "25:00", "5", "15", 4)
        logMessage(TAG, "pomodoro insert result: $result")
        result = insertTimerData(db, "30/30", "30:00", "30", "60", 2)
        logMessage(TAG, "30/30 insert result: $result")
        result = insertTimerData(db, "Free Flow", "45:00", "15", "30", 2)
        logMessage(TAG, "Free Flow insert result: $result")
    }

    /**
     * Create a table for storing timers, insert default timer data if the table is 'default_timers'
     */
    fun createTimerTable(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS $table(name TEXT primary key, time_work TEXT, time_short_break TEXT, time_long_break TEXT, intervals INTEGER);")
        if (table == "default_timers") {
            insertDefaultTimerData(db)
        }
    }

    /**
     * Create a table for storing tasks
     */
    fun createTaskTable(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS $table(name TEXT primary key, type TEXT, date TEXT, time TEXT, priority TEXT, completed TEXT);")
    }

    /**
     * Insert a Timer
     */
    fun insertTimerData(db: SQLiteDatabase, name: String, timeWork: String, timeShortBreak: String, timeLongBreak: String, intervals: Int): Boolean {
        val contentValues = contentValuesOf()
        contentValues.put("name", name)
        contentValues.put("time_work", timeWork)
        contentValues.put("time_short_break", timeShortBreak)
        contentValues.put("time_long_break", timeLongBreak)
        contentValues.put("intervals", intervals)
        val result = db.insert(table, null, contentValues)
        return result != (-1).toLong()
    }

    /**
     * Insert a Task
     */
    fun insertTaskData(db: SQLiteDatabase, name: String, type: String, date: String, time: String, priority: String, completed: Boolean): Boolean {
        val contentValues = contentValuesOf()
        contentValues.put("name", name)
        contentValues.put("type", type)
        contentValues.put("date", date)
        contentValues.put("time", time)
        contentValues.put("priority", priority)
        contentValues.put("completed", completed.toString())
        val result = db.insert(table, null, contentValues)
        return result != (-1).toLong()
    }

    /**
     * Fetch all data from the instance table
     */
    fun readAllData(db: SQLiteDatabase): Cursor {
        return db.rawQuery("SELECT * FROM $table;", null)
    }

    /**
     * Delete the given data
     */
    fun deleteData(db: SQLiteDatabase, name: String): Boolean {
        val result = db.delete(table, "name='$name';", null)
        return result == 1
    }

    /**
     * Check if the isntance table is empty
     */
    fun isTableEmpty(db: SQLiteDatabase): Boolean {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $table;", null)
        if (cursor!!.moveToFirst()) {
            if (cursor.getInt(0) == 0) {
                cursor.close()
                return true
            }
        }
        cursor.close()
        return false
    }

    /**
     * Check if the given Timer already exists in the database
     */
    fun timerExists(db: SQLiteDatabase, timer: Timer): Boolean {
        val cursor = db.rawQuery("SELECT name FROM $table WHERE name='${timer.name}';", null)
        if (cursor.count > 0) {
            cursor.close()
            return true
        }
        cursor.close()
        return false
    }

    /**
     * Check if the given Task already exists in the database
     */
    fun taskExists(db: SQLiteDatabase, task: Task): Boolean {
        val cursor = db.rawQuery("SELECT name, date, time FROM $table WHERE name='${task.name}' AND date='${task.date}' AND time='${task.time}';", null)
        if (cursor.count > 0) {
            cursor.close()
            return true
        }
        cursor.close()
        return false
    }

    /**
     * Update the completion status of the given Task
     */
    fun changeTaskCompletion(db: SQLiteDatabase, task: Task) {
        logMessage(TAG, "updating task completion to ${task.name} / ${task.completed}")
        val contentValues = contentValuesOf()
        contentValues.put("completed", task.completed.toString())
        db.update(table, contentValues, "name=? AND date=? and time=?", arrayOf(task.name, task.date, task.time))
    }
}