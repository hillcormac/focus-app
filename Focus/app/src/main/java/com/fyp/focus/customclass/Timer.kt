package com.fyp.focus.customclass

class Timer(
    val name: String,
    val timeWork: String,
    timeShortBreak: String,
    timeLongBreak: String,
    val intervals: Int
) {

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