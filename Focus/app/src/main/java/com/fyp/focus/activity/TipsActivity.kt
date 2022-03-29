package com.fyp.focus.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.fyp.focus.R
import com.fyp.focus.adapter.TipsPagerAdapter
import com.fyp.focus.customclass.Tip
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

private const val TAG = "TipsActivity"

class TipsActivity : AppCompatActivity() {

    private lateinit var adapter: TipsPagerAdapter
    private val tipsArray = ArrayList<Tip>()
    private lateinit var dotsIndicator: WormDotsIndicator
    private lateinit var vpTips: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tips)
        supportActionBar?.hide()
        dotsIndicator = findViewById(R.id.dotsIndicator)
        vpTips = findViewById(R.id.vpTips)

        tipsArray.add(Tip(getString(R.string.tips_heading_1), R.drawable.using_timers, getString(R.string.tips_text_1)))
        tipsArray.add(Tip(getString(R.string.tips_heading_2), R.drawable.track_your_tasks, getString(R.string.tips_text_2)))
        tipsArray.add(Tip(getString(R.string.tips_heading_3), R.drawable.tidy_environment, getString(R.string.tips_text_3)))
        tipsArray.add(Tip(getString(R.string.tips_heading_4), R.drawable.remove_distractions, getString(R.string.tips_text_4)))
        tipsArray.add(Tip(getString(R.string.tips_heading_5), R.drawable.form_rituals, getString(R.string.tips_text_5)))
        tipsArray.add(Tip(getString(R.string.tips_heading_6), R.drawable.dont_multitask, getString(R.string.tips_text_6)))
        tipsArray.add(Tip(getString(R.string.tips_heading_7), R.drawable.listen_to_music, getString(R.string.tips_text_7)))
        tipsArray.add(Tip(getString(R.string.tips_heading_8), R.drawable.take_a_break, getString(R.string.tips_text_8)))
        tipsArray.add(Tip(getString(R.string.tips_heading_9), R.drawable.what_works_for_you, getString(R.string.tips_text_9)))

        adapter = TipsPagerAdapter(this, tipsArray)
        vpTips.adapter = adapter
        dotsIndicator.setViewPager(vpTips)
    }
}