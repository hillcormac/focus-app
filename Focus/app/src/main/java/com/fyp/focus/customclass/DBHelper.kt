package com.fyp.focus.customclass

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import com.fyp.focus.global.GlobalFunctions.logMessage

private const val TAG = "DBHelper"

class DBHelper(
    context: Context,
    private val dbName: String,
    private val table: String
): SQLiteOpenHelper(context, dbName, null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $table(name TEXT primary key, time_work TEXT, time_short_break TEXT, time_long_break TEXT, intervals INTEGER);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $table(name TEXT primary key, time_work TEXT, time_short_break TEXT, time_long_break TEXT, intervals INTEGER);")
    }

    private fun insertDefaultData(db: SQLiteDatabase) {
        var result = insertData(db, "pomodoro", "25:00", "5", "15", 4)
//        logMessage(TAG, "pomodoro insert result: $result")
        result = insertData(db, "30/30", "30:00", "30", "60", 2)
//        logMessage(TAG, "30/30 insert result: $result")
        result = insertData(db, "Free Flow", "45:00", "15", "30", 2)
//        logMessage(TAG, "Free Flow insert result: $result")
    }

    fun createTable(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS $table(name TEXT primary key, time_work TEXT, time_short_break TEXT, time_long_break TEXT, intervals INTEGER);")
        if (table == "default_timers") {
            insertDefaultData(db)
        }
    }

    fun insertData(db: SQLiteDatabase, name: String, timeWork: String, timeShortBreak: String, timeLongBreak: String, intervals: Int): Boolean {
        val contentValues = contentValuesOf()
        contentValues.put("name", name)
        contentValues.put("time_work", timeWork)
        contentValues.put("time_short_break", timeShortBreak)
        contentValues.put("time_long_break", timeLongBreak)
        contentValues.put("intervals", intervals)
        val result = db.insert(table, null, contentValues)
        return result != (-1).toLong()
    }

    fun readAllData(db: SQLiteDatabase): Cursor {
        return db.rawQuery("SELECT * FROM $table", null)
    }

    fun deleteData(db: SQLiteDatabase, name: String): Boolean {
        val result = db.delete(table, "name='$name'", null)
        return result == 1
    }

    fun isTableEmpty(db: SQLiteDatabase): Boolean {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $table", null)
        if (cursor!!.moveToFirst()) {
            if (cursor.getInt(0) == 0) {
                return true
            }
        }
        return false
    }
}