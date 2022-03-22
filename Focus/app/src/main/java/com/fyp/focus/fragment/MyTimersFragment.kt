package com.fyp.focus.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.fyp.focus.R
import com.fyp.focus.activity.TimerActivity
import com.fyp.focus.adapter.TimerListAdapter
import com.fyp.focus.customclass.DBHelper
import com.fyp.focus.customclass.Timer
import com.fyp.focus.dialog.ConfirmDeleteTimerDialogFragment
import com.fyp.focus.dialog.CreateCustomTimerDialogFragment
import com.fyp.focus.global.GlobalFunctions.logMessage
import com.fyp.focus.global.UserPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val TAG = "MyTimersFragment"

class MyTimersFragment : Fragment(),
    CreateCustomTimerDialogFragment.CreateCustomTimerDialogListener,
    ConfirmDeleteTimerDialogFragment.ConfirmDeleteTimerDialogListener {

    private lateinit var preferences: UserPreferences
    private lateinit var dbHelper: DBHelper
    private lateinit var tvMyTimersList: TextView
    private lateinit var fabMyTimers: FloatingActionButton

    private var timerArray =  ArrayList<Timer>()
    private lateinit var adapter: TimerListAdapter

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
        adapter = TimerListAdapter(this, timerArray)

        if (!preferences.customTimerDbCreated) {
//            logMessage(TAG, "table not created, creating now")
            dbHelper.createTimerTable(dbHelper.writableDatabase)
            preferences.customTimerDbCreated = true
        }

        if (dbHelper.isTableEmpty(dbHelper.readableDatabase)) {
            tvMyTimersList.visibility = View.VISIBLE
        } else {
            tvMyTimersList.visibility = View.GONE
            timerArray = ArrayList()

            val cursor = dbHelper.readAllData(dbHelper.readableDatabase)
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    val timer = Timer(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4))
                    timerArray.add(timer)
//                logMessage(TAG, "timer: $timer")
                    cursor.moveToNext()
                }
            }

            adapter = TimerListAdapter(this, timerArray)
            val listView = requireView().findViewById<ListView>(R.id.lvMyTimers)
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

            listView.setOnItemLongClickListener { adapterView, view, i, l ->
                val dialog = ConfirmDeleteTimerDialogFragment(adapter.timers[i])
                dialog.setTargetFragment(this, 1)
                dialog.show(requireActivity().supportFragmentManager, "ConfirmDeleteTimerDialog")
                true
            }
        }

        fabMyTimers.setOnClickListener {
            val dialog = CreateCustomTimerDialogFragment(dbHelper)
            dialog.setTargetFragment(this, 1)
            dialog.show(requireActivity().supportFragmentManager, "CreateCustomTimerDialog")
        }
    }

    override fun onTimerCreated(dialog: DialogFragment, timer: Timer) {
        logMessage(TAG, "received timer $timer")
        dbHelper.insertTimerData(
            dbHelper.writableDatabase,
            timer.name, timer.timeWork,
            timer.timeShortBreak.toString(),
            timer.timeLongBreak.toString(),
            timer.intervals
        )
        timerArray.add(timer)
        adapter.notifyDataSetChanged()
        if (adapter.timers.isNotEmpty()) {
            tvMyTimersList.visibility = View.GONE
        }
    }

    override fun onDeleteTimer(dialog: DialogFragment, timer: Timer) {
        dbHelper.deleteData(dbHelper.writableDatabase, timer.name)
        adapter.remove(timer)
        adapter.notifyDataSetChanged()
        if (adapter.timers.isEmpty()) {
            tvMyTimersList.visibility = View.VISIBLE
        }
    }
}