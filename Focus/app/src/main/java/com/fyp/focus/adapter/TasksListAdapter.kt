package com.fyp.focus.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fyp.focus.R
import com.fyp.focus.customclass.DBHelper
import com.fyp.focus.customclass.Task


private const val TAG = "TasksListAdapter"

class TasksListAdapter(private val context: Context, private val dbHelper: DBHelper, private val items: ArrayList<Pair<String, ArrayList<Task>>>):
    RecyclerView.Adapter<TasksListAdapter.ViewHolder>() {

        class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val tvDateHeading: TextView = view.findViewById(R.id.tvDateHeading)
            val btnExpandDate: ImageButton = view.findViewById(R.id.btnExpandDate)
            val rvTasks: RecyclerView = view.findViewById(R.id.rvTasks)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.array_adapter_tasks_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvDateHeading.text = items[position].first
        holder.btnExpandDate.setOnClickListener {
            // add animation and functionality to expansion button
            val deg = if (holder.btnExpandDate.rotation == 180f) 0f else 180f
            holder.btnExpandDate.animate().rotation(deg).interpolator = AccelerateDecelerateInterpolator()
            holder.rvTasks.visibility = when (holder.rvTasks.visibility) {
                View.VISIBLE -> View.GONE
                View.GONE -> View.VISIBLE
                else -> View.VISIBLE
            }
        }
        // initialise internal RecyclerView and attach adapter of individual tasks
        holder.rvTasks.layoutManager = LinearLayoutManager(context)
        holder.rvTasks.adapter = TasksAdapter(context, dbHelper, items[position].second)
    }

    override fun getItemCount() = items.size
}