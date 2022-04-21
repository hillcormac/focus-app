package com.fyp.focus.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.fyp.focus.R
import com.fyp.focus.customclass.DBHelper
import com.fyp.focus.customclass.Timer
import com.fyp.focus.global.GlobalFunctions.toastMessage
import java.lang.ClassCastException
import java.lang.IllegalStateException

private const val TAG = "CreateCustomTimerDialogFragment"

class CreateCustomTimerDialogFragment(private val dbHelper: DBHelper): DialogFragment() {

    private lateinit var listener: CreateCustomTimerDialogListener

    private var etTimerName: EditText? = null
    private var etTimeWorkMins: EditText? = null
    private var etTimeWorkSecs: EditText? = null
    private var etTimeShortBreak: EditText? = null
    private var etTimeLongBreak: EditText? = null
    private var etTimerIntervals: EditText? = null

    interface CreateCustomTimerDialogListener {
        fun onTimerCreated(dialog: DialogFragment, timer: Timer)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // initialise listener
        try {
            listener = targetFragment as CreateCustomTimerDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement CreateCustomTimerDialogFragment")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            // initialise components
            val dialogView = this.layoutInflater.inflate(R.layout.dialog_create_custom_timer, null)
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Custom Timer")
            builder.setView(dialogView)
            etTimerName = dialogView.findViewById(R.id.etTimerName)
            etTimeWorkMins = dialogView.findViewById(R.id.etTimeWorkMins)
            etTimeWorkSecs = dialogView.findViewById(R.id.etTimeWorkSecs)
            etTimeShortBreak = dialogView.findViewById(R.id.etTimeShortBreak)
            etTimeLongBreak = dialogView.findViewById(R.id.etTimeLongBreak)
            etTimerIntervals = dialogView.findViewById(R.id.etTimerIntervals)
            builder.setPositiveButton("Create Timer") { dialog, _ ->
                val (errorMessage, valid) = allFieldsValid()
                if (valid) {
                    // if all fields of the dialog have valid value
                    if (etTimeWorkSecs?.text.toString().length == 1) {
                        etTimeWorkSecs?.setText(etTimeWorkSecs?.text.toString().padStart(2, '0'))
                    }
                    // create Timer instance
                    val timer = Timer(
                        etTimerName?.text.toString(),
                        "${etTimeWorkMins?.text}:${etTimeWorkSecs?.text}",
                        etTimeShortBreak?.text.toString(),
                        etTimeLongBreak?.text.toString(),
                        etTimerIntervals?.text.toString().toInt()
                    )
                    // check if Timer already exists, if not pass to listener function
                    if (dbHelper.timerExists(dbHelper.readableDatabase, timer)) {
                        toastMessage(requireContext(), "Timer with name ${timer.name} already exists")
                    } else {
                        listener.onTimerCreated(this, timer)
                        dialog.dismiss()
                    }
                } else {
                    toastMessage(requireContext(), errorMessage)
                }
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    /**
     * Check if all fields of the dialog contain valid values
     *
     * @return Pair of string stating reason for invalid and Boolean
     */
    private fun allFieldsValid(): Pair<String, Boolean> {
        // if any fields are empty
        if (TextUtils.isEmpty(etTimerName?.text.toString()) || TextUtils.isEmpty(etTimeWorkMins?.text.toString()) || TextUtils.isEmpty(etTimeWorkSecs?.text.toString())
            || TextUtils.isEmpty(etTimeShortBreak?.text.toString()) || TextUtils.isEmpty(etTimeLongBreak?.text.toString()) || TextUtils.isEmpty(etTimerIntervals?.text.toString())) {
            return "Please fill in all fields" to false
        } else if (etTimeWorkMins?.text.toString().toInt() < 1 || etTimeShortBreak?.text.toString().toInt() < 1
            || etTimeLongBreak?.text.toString().toInt() < 1 || etTimerIntervals?.text.toString().toInt() < 1) {
            return "Minute and Interval fields cannot be 0 " to false
        } else if (etTimeWorkSecs?.text.toString().toInt() > 59) {
            return "Seconds cannot be more than 60" to false
        }
        return "" to true
    }
}