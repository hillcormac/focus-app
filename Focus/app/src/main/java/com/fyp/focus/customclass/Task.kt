package com.fyp.focus.customclass

/**
 * Represents a user defined task to be complete
 *
 * @param name the name of the task
 * @param type the type of the task (all types are user defined)
 * @param date the deadline date of the task
 * @param time the deadline time of the task
 * @param priority the priority of the task
 * @param completed if the task has been completed or not
 */
class Task (
    val name: String,
    val type: String,
    val date: String,
    val time: String,
    val priority: String,
    var completed: Boolean = false
) {
    override fun toString(): String {
        return """
            name: $name
            type: $type
            deadline: $date - $time
            priority: $priority
            completed: $completed
        """.trimIndent()
    }
}