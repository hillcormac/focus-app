package com.fyp.focus.customclass


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