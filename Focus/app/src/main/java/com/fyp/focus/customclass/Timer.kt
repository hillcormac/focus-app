package com.fyp.focus.customclass

/**
 * Represents a user defined timer
 *
 * @param name the name of the timer
 * @param timeWork the time the user will work for
 * @param timeShortBreak the time the user will take for a short break
 * @param timeLongBreak the time the user will take for a long break
 * @param intervals the number of work and break phases the user will take before completing a cycle of the timer
 */
class Timer(
    val name: String,
    val timeWork: String,
    timeShortBreak: String,
    timeLongBreak: String,
    val intervals: Int
) {

    // convert times to Long (milliseconds) for logic use
    val splitTimeWork = timeWork.split(":")
    var timeWorkMins = splitTimeWork[0].toInt()
    var timeWorkSecs = splitTimeWork[1].toInt()
    var timeWorkMillis = (((timeWorkMins * 60) + timeWorkSecs) * 1000).toLong()

    val timeShortBreak = timeShortBreak.toInt()
    var timeShortBreakMillis = ((this.timeShortBreak * 60) * 1000).toLong()

    val timeLongBreak = timeLongBreak.toInt()
    var timeLongBreakMillis = ((this.timeLongBreak * 60) * 1000).toLong()

    override fun toString(): String {
        return """
            name: $name
            timeWork: $timeWork
            splitTimes: $timeWorkMins mins, $timeWorkSecs secs, $timeWorkMillis millis
            shortBreak: $timeShortBreak mins, $timeShortBreakMillis millis 
            longBreak: $timeLongBreak mins, $timeLongBreakMillis millis
            intervals: $intervals
        """.trimIndent()
    }
}