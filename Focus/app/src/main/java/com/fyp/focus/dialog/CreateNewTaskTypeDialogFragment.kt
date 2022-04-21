package com.fyp.focus.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.fyp.focus.R
import com.fyp.focus.global.GlobalFunctions.toastMessage
import com.fyp.focus.global.GlobalVariables
import java.lang.ClassCastException

import java.lang.IllegalStateException

private const val TAG = "CreateNewTaskTypeDialogFragment"

class CreateNewTaskTypeDialogFragment(): DialogFragment() {

    private lateinit var listener: CreateNewTaskTypeDialogListener
    private var etNewTaskType: EditText? = null

    interface CreateNewTaskTypeDialogListener {
        fun onTaskTypeCreated(dialog: DialogFragment, newType: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // initialise listener
        try {
            listener = targetFragment as CreateNewTaskTypeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement CreateNewTaskTypeDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val dialogView = this.layoutInflater.inflate(R.layout.dialog_new_task_type, null)
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Create a New Task Type")
            builder.setView(dialogView)

            // initialise components
            etNewTaskType = dialogView.findViewById(R.id.etNewTaskType)

            builder.setPositiveButton("OK") { dialog, _ ->
                // check if field has a valid value
                if (TextUtils.isEmpty(etNewTaskType?.text.toString()) || etNewTaskType?.text.toString() == "null") {
                    toastMessage(requireContext(), "Please fill in the new task type field")
                } else {
                    GlobalVariables.taskTypes.add(etNewTaskType?.text.toString())
                    listener.onTaskTypeCreated(this, etNewTaskType?.text.toString())
                }
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}