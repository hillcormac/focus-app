package com.fyp.focus.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fyp.focus.R
import com.fyp.focus.customclass.DBHelper
import com.fyp.focus.customclass.Task
import com.fyp.focus.global.GlobalFunctions.logMessage
import com.fyp.focus.global.GlobalFunctions.toastMessage

private const val TAG = "TasksAdapter"

class TasksAdapter(private val context: Context, private val dbHelper: DBHelper, private val items: ArrayList<Task>):
    RecyclerView.Adapter<TasksAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvTaskName: TextView = view.findViewById(R.id.tvTaskName)
        val cbTaskComplete: CheckBox = view.findViewById(R.id.cbTaskComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.array_adapter_task, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTaskName.text = items[position].name
        holder.tvTaskName.setOnClickListener {
            toastMessage(context, "clicked task ${holder.tvTaskName.text}")
        }
        if (items[position].completed) {
            holder.tvTaskName.paintFlags = holder.tvTaskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.cbTaskComplete.isChecked = true
        }

        holder.cbTaskComplete.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.tvTaskName.paintFlags = holder.tvTaskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                items[position].completed = true
            } else {
                holder.tvTaskName.paintFlags = holder.tvTaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                items[position].completed = false
            }
            dbHelper.changeTaskCompletion(dbHelper.writableDatabase, items[position])
        }
    }

    override fun getItemCount() = items.size
}