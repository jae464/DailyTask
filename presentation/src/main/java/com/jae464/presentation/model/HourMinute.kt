package com.jae464.presentation.model

data class HourMinuteSecond(
    val hour: Int,
    val minute: Int,
    val second: Int,
) {
    companion object {
        fun from(time: Int): HourMinuteSecond {
            val hour = (time / 3600)
            val minute = time % 3600 / 60
            val second = time / 3600 % 60
            return HourMinuteSecond(hour, minute, second)
        }

        fun toInt(hourMinuteSecond: HourMinuteSecond): Int {
            return hourMinuteSecond.hour * 3600 + hourMinuteSecond.minute * 60 + hourMinuteSecond.second
        }
    }
}
