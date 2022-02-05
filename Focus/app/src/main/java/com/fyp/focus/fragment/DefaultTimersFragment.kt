package com.fyp.focus.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fyp.focus.R
import com.fyp.focus.activity.TimerActivity

private const val TAG = "DefaultTimersFragment"

class DefaultTimersFragment : Fragment() {

    private lateinit var tvPomodoro: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_default_timers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPomodoro = requireView().findViewById(R.id.tvPomodoro)
        tvPomodoro.setOnClickListener {
            val intent = Intent(context, TimerActivity::class.java)
            intent.putExtra("timerName", "pomodoro")
            intent.putExtra("timeWork", 25)
            intent.putExtra("timeShortBreak", 5)
            intent.putExtra("intervals", 4)
            startActivity(intent)
        }
    }


}