package com.jae464.data.database.util

import androidx.core.text.isDigitsOnly
import androidx.room.TypeConverter
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.HourMinute
import com.jae464.domain.model.TaskType
import java.time.LocalDateTime

class HourMinuteConverter {
    @TypeConverter
    fun hourMinuteToString(hourMinute: HourMinute?): String? {
        return hourMinute?.let {
            "%2d:%2d".format(it.hour, it.minute)
        }
    }

    @TypeConverter
    fun stringToHourMinute(value: String?): HourMinute? {
        val data = value?.split(":") ?: return null
        if (data.size == 2 && data.all { it.isDigitsOnly() }) {
            return HourMinute(data[0].toInt(), data[1].toInt())
        }
        return null
    }
}

class TaskTypeConverter {
    @TypeConverter
    fun taskTypeToString(taskType: TaskType?): String? =
        taskType?.let(TaskType::taskName)

    @TypeConverter
    fun stringToTaskType(value: String?): TaskType? {
        return TaskType.values().firstOrNull {
            it.taskName == value
        }
    }
}

class DayOfWeekConverter {
    @TypeConverter
    fun dayOfWeekToString(dayOfWeek: DayOfWeek?): String? =
        dayOfWeek?.let(DayOfWeek::day)

    @TypeConverter
    fun stringToDayOfWeek(value: String?): DayOfWeek? {
        return DayOfWeek.values().firstOrNull {
            it.day == value
        }
    }
}

// TODO List로 되어있는거 수정하기
class LocalDateTimeConverter {
    @TypeConverter
    fun localDateTimesToString(localDateTime: LocalDateTime?): String? {
        return localDateTime.toString()
    }

    @TypeConverter
    fun stringToLocalDateTimes(value: String?): LocalDateTime? {
        return LocalDateTime.parse(value)
    }
}