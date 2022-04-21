package com.fyp.focus.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.fyp.focus.customclass.Timer
import java.lang.ClassCastException

class ConfirmDeleteTimerDialogFragment(private val timer: Timer): DialogFragment() {

    private lateinit var listener: ConfirmDeleteTimerDialogListener

    interface ConfirmDeleteTimerDialogListener {
        fun onDeleteTimer(dialog: DialogFragment, timer: Timer)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // initialise listener
        try {
            listener = targetFragment as ConfirmDeleteTimerDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement ConfirmDeleteTimerDialogFragment")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            // create builder and initialise components
            builder.setTitle("Delete Timer")
                .setMessage("Are you sure you want to delete ${timer.name}?")
                .setPositiveButton("Delete") { dialog, _ ->
                    listener.onDeleteTimer(this, timer)
                }
                .setNeutralButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}