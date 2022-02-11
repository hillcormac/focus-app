package com.fyp.focus.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fyp.focus.R
import com.fyp.focus.customclass.Timer
import com.fyp.focus.global.GlobalFunctions.logMessage

private const val TAG = "TimerListAdapter"

class TimerListAdapter(
    private val context: Fragment,
    val timers: ArrayList<Timer>
):
    ArrayAdapter<Timer>(context.requireContext(), R.layout.array_adapter_timer_list, timers) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView: View = inflater.inflate(R.layout.array_adapter_timer_list, null, true)
        val timerName = rowView.findViewById<TextView>(R.id.tvTimerName)
        val workTimer = rowView.findViewById<TextView>(R.id.tvWorkTime)
        val shortBreakTimer = rowView.findViewById<TextView>(R.id.tvShortTime)
        val longBreakTimer = rowView.findViewById<TextView>(R.id.tvLongTime)

        timerName?.text = timers[position].name
        workTimer?.text = timers[position].timeWork
        shortBreakTimer?.text = "${timers[position].timeShortBreak}:00"
        longBreakTimer?.text = "${timers[position].timeLongBreak}:00"

        return rowView
    }
}