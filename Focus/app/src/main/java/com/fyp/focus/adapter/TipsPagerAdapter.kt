package com.fyp.focus.adapter

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.fyp.focus.R
import com.fyp.focus.customclass.Tip

class TipsPagerAdapter(
    private val context: Context,
    private val tipsArray: ArrayList<Tip>,
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
): PagerAdapter() {


    override fun getCount(): Int {
        return tipsArray.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = layoutInflater.inflate(R.layout.array_adapter_tips, container, false)
        val tvTipsHeading = view.findViewById<TextView>(R.id.tvTipsHeading)
        val ivTipsImage = view.findViewById<ImageView>(R.id.ivTipsImage)
        val tvTipsText = view.findViewById<TextView>(R.id.tvTipsText)
        tvTipsHeading.text = tipsArray[position].name
        ivTipsImage.setImageResource(tipsArray[position].imageResId)
        tvTipsText.movementMethod = ScrollingMovementMethod()
        tvTipsText.text = tipsArray[position].text
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}