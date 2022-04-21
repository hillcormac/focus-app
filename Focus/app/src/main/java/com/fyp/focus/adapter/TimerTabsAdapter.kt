package com.fyp.focus.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.fyp.focus.fragment.DefaultTimersFragment
import com.fyp.focus.fragment.MyTimersFragment

private const val TAG = "TimerTabsAdapter"

internal class TimerTabsAdapter(var context: Context, fm: FragmentManager, var totalTabs: Int): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // return corresponding Fragment
        return when(position){
            0 -> {
                DefaultTimersFragment()
            }
            1 -> {
                MyTimersFragment()
            }
            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }


}