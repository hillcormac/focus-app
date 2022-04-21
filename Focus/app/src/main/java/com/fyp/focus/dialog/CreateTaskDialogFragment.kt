package com.fyp.focus.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.fyp.focus.R
import com.fyp.focus.customclass.DBHelper
import com.fyp.focus.customclass.Task
import com.fyp.focus.global.GlobalFunctions.logMessage
import com.fyp.focus.global.GlobalFunctions.toastMessage
import com.fyp.focus.global.GlobalVariables
import com.fyp.focus.global.UserPreferences
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.lang.ClassCastException
import java.lang.IllegalStateException
import java.util.*

private const val TAG = "CreateTaskDialogFragment"

class CreateTaskDialogFragment(private val dbHelper: DBHelper): DialogFragment(), CreateNewTaskTypeDialogFragment.CreateNewTaskTypeDialogListener {

    private lateinit var listener: CreateTaskDialogListener

    private var etTaskName: EditText? = null
    private var spnTaskType: Spinner? = null
    private var tvTaskDeadline: TextView? = null
    private var spnTaskPriority: Spinner? = null
    private var btnNewTaskType: ImageButton? = null

    private lateinit var taskTypeAdapter: ArrayAdapter<String>

    private var taskTypeSelected: String? = null
    private var taskDateSelected: String? = null
    private var taskTimeSelected: String? = null
    private var taskPrioritySelected: String? = null

    interface CreateTaskDialogListener {
        fun onTaskCreated(dialog: DialogFragment, task: Task)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // initialise listener
        try {
            listener = context as CreateTaskDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement CreateTaskDialogFragment")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            // initialise components
            val dialogView = this.layoutInflater.inflate(R.layout.dialog_create_task, null)
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Create a Task")
            builder.setView(dialogView)
            etTaskName = dialogView.findViewById(R.id.etTaskName)
            spnTaskType = dialogView.findViewById(R.id.spnTaskType)
            tvTaskDeadline = dialogView.findViewById(R.id.tvTaskDeadline)
            spnTaskPriority = dialogView.findViewById(R.id.spnTaskPriority)
            btnNewTaskType = dialogView.findViewById(R.id.btnNewTaskType)

            logMessage(TAG, "current time: ${Calendar.getInstance().time}")

            btnNewTaskType?.setOnClickListener {
                val dialog = CreateNewTaskTypeDialogFragment()
                dialog.setTargetFragment(this, 1)
                dialog.show(requireActivity().supportFragmentManager, "CreateNewTaskTypeDialog")
            }

            logMessage(TAG, "taskTypes: ${GlobalVariables.taskTypes}")
            taskTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, GlobalVariables.taskTypes)
            taskTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnTaskType?.adapter = taskTypeAdapter

            // initialise spinner for task type
            spnTaskType?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position != 0) {
                        taskTypeSelected = parent?.getItemAtPosition(position) as String
                        logMessage(TAG, "taskTypeSelected = $taskTypeSelected")
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    toastMessage(requireContext(), "Please select an option from $parent")
                }
            }

            // initialise spinner for task priority
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.task_priorities,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnTaskPriority?.adapter = adapter
            }

            spnTaskPriority?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position != 0) {
                        taskPrioritySelected = parent?.getItemAtPosition(position) as String
                        logMessage(TAG, "taskPrioritySelected = $taskPrioritySelected")
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    toastMessage(requireContext(), "Please select an option from $parent")
                }
            }

            // initialise date picker for task deadline
            tvTaskDeadline?.setOnClickListener {
                val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
                val constraints = CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())
                    .build()
                datePickerBuilder.setCalendarConstraints(constraints)
                val datePicker = datePickerBuilder.build()
                datePicker.addOnPositiveButtonClickListener {
                    val calendar = Calendar.getInstance()
                    calendar.time = Date(it)
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)+1
                    var monthStr = month.toString()
                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    var dayStr = day.toString()

                    if (month < 10) {
                        monthStr = "0$month"
                    }
                    if (day < 10) {
                        dayStr = "0$day"
                    }
                    taskDateSelected = "$year-$monthStr-$dayStr"
                    tvTaskDeadline!!.text = taskDateSelected!!

                    val timePicker = MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0)
                        .build()
                    timePicker.addOnPositiveButtonClickListener {
                        taskTimeSelected = if (timePicker.minute == 0) {
                            "${timePicker.hour}:00"
                        } else {
                            "${timePicker.hour}:${timePicker.minute}"
                        }

                        tvTaskDeadline!!.text = "${tvTaskDeadline!!.text} - $taskTimeSelected"
                    }
                    timePicker.show(requireActivity().supportFragmentManager, "TimePickerDialog")
                }
                datePicker.show(requireActivity().supportFragmentManager, "DatePickerDialog")
            }

            builder.setPositiveButton("Create Task") { dialog, _ ->
                val (errorMessage, valid) = allFieldsValid()
                if (valid) {
                    // if all fields have valid values
                    val task = Task(
                        etTaskName?.text.toString(),
                        taskTypeSelected!!,
                        taskDateSelected!!,
                        taskTimeSelected!!,
                        taskPrioritySelected!!
                    )
                    // check if the Task already exists, if not pass to listener function
                    if (dbHelper.taskExists(dbHelper.readableDatabase, task)) {
                        toastMessage(requireContext(), "Task  '${task.name}' @ '${task.date} - ${task.time}' already exists")
                    } else {
                        listener.onTaskCreated(this, task)
                        dialog.dismiss()
                    }
                    logMessage(TAG, "created task $task")
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
        if (TextUtils.isEmpty(etTaskName?.text.toString()) || taskTypeSelected == null || taskDateSelected == null
            || taskTimeSelected == null || taskPrioritySelected == null) {
            return "Please fill in all fields" to false
        }
        return "" to true
    }

    /**
     * Updates list of task types when a new type is created
     *
     * @param dialog calling dialog
     * @param newType the new type that was added
     */
    override fun onTaskTypeCreated(dialog: DialogFragment, newType: String) {
        taskTypeAdapter.notifyDataSetChanged()
        val preferences = UserPreferences(requireContext())
        preferences.taskTypeArray = "${preferences.taskTypeArray}$newType,"
        logMessage(TAG, "updated taskTypeArray: ${preferences.taskTypeArray}")
    }
}