package com.fyp.focus.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.fyp.focus.R
import com.fyp.focus.activity.TimerActivity
import com.fyp.focus.adapter.TimerListAdapter
import com.fyp.focus.customclass.Timer
import com.fyp.focus.global.GlobalFunctions.logMessage

private const val TAG = "DefaultTimersFragment"

class DefaultTimersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_default_timers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pomodoro = Timer("pomodoro", "25:00", "5", "15", 4)
        val thirtyThirty = Timer("30/30", "30:00", "30", "60", 2)
        val freeFlow = Timer("Free Flow", "45:00", "15", "30", 2)

        val timerArray = ArrayList<Timer>()
        timerArray.add(pomodoro)
        timerArray.add(thirtyThirty)
        timerArray.add(freeFlow)

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