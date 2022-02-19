package com.fyp.focus.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import com.fyp.focus.R
import com.fyp.focus.activity.TimerActivity
import com.fyp.focus.adapter.TimerListAdapter
import com.fyp.focus.customclass.DBHelper
import com.fyp.focus.customclass.Timer
import com.fyp.focus.global.GlobalFunctions.logMessage
import com.fyp.focus.global.UserPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val TAG = "MyTimersFragment"

class MyTimersFragment : Fragment() {

    private lateinit var preferences: UserPreferences
    private lateinit var dbHelper: DBHelper
    private lateinit var tvMyTimersList: TextView
    private lateinit var fabMyTimers: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_timers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvMyTimersList = view.findViewById(R.id.tvMyTimersList)
        fabMyTimers = view.findViewById(R.id.fabMyTimers)
        dbHelper = DBHelper(requireContext(), "focus", "custom_timers")
        preferences = UserPreferences(requireContext())

        if (!preferences.customTimerDbCreated) {
//            logMessage(TAG, "table not created, creating now")
            dbHelper.createTable(dbHelper.writableDatabase)
            preferences.customTimerDbCreated = true
        }

        if (dbHelper.isTableEmpty(dbHelper.readableDatabase)) {
            tvMyTimersList.visibility = View.VISIBLE
        } else {
            initListView()
        }

        fabMyTimers.setOnClickListener {

        }
    }

    private fun initListView() {
        tvMyTimersList.visibility = View.GONE
        val timerArray = ArrayList<Timer>()

        val cursor = dbHelper.readAllData(dbHelper.readableDatabase)
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val timer = Timer(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4))
                timerArray.add(timer)
//                logMessage(TAG, "timer: $timer")
                cursor.moveToNext()
            }
        }

        val adapter = TimerListAdapter(this, timerArray)
        val listView = requireView().findViewById<ListView>(R.id.lvDefaultTimers)
        listView.adapter = adapter

        listView.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(context, TimerActivity::class.java)
//            logMessage(TAG, "intent extras: ${adapter.timers[i].name}, ${adapter.timers[i].timeWork}. ${adapter.timers[i].timeShortBreak}, ${adapter.timers[i].timeLongBreak}, ${adapter.timers[i].intervals}")
            intent.putExtra("timerName", adapter.timers[i].name)
            intent.putExtra("timeWork", adapter.timers[i].timeWork)
            intent.putExtra("timeShortBreak", adapter.timers[i].timeShortBreak.toString())
            intent.putExtra("timeLongBreak", adapter.timers[i].timeLongBreak.toString())
            intent.putExtra("intervals", adapter.timers[i].intervals.toString())
            startActivity(intent)
        }
    }
}