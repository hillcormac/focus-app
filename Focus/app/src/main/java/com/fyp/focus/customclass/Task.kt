package com.fyp.focus.customclass


class Task (
    val name: String,
    val date: String,
    val type: String,
    val priority: String,
    val completed: Boolean = false
) {
    override fun toString(): String {
        return """
            name: $name
            date: $date
            type: $type
            priority: $priority
            completed: $completed
        """.trimIndent()
    }
}