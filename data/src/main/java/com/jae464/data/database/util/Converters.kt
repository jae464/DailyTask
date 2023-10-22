package com.jae464.data.database.util

import androidx.core.text.isDigitsOnly
import androidx.room.TypeConverter
import androidx.room.util.joinIntoString
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.HourMinute
import com.jae464.domain.model.TaskType
import java.time.LocalDate
import java.time.LocalDateTime

class HourMinuteConverter {
    @TypeConverter
    fun hourMinuteToString(hourMinute: HourMinute?): String? {
        return hourMinute?.let {
            "${it.hour}:${it.minute}"
        }
    }

    @TypeConverter
    fun stringToHourMinute(value: String?): HourMinute? {
        val data = value?.split(":") ?: return null
        println(data)
        if (data.size == 2) {
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
    fun dayOfWeeksToString(dayOfWeeks: List<DayOfWeek>?): String? {
       return dayOfWeeks?.joinToString(separator = ",") {
           it.day
       }
    }

    @TypeConverter
    fun stringToDayOfWeek(value: String?): List<DayOfWeek>? {
        if (value?.isEmpty() == true) return emptyList()
        return value?.split(",")?.map { day ->
            DayOfWeek.values().first {
                day == it.day
            }
        }
    }
}

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

class LocalDateConverter {
    @TypeConverter
    fun localDateToString(localDate: LocalDate?): String {
        return localDate.toString()
    }

    @TypeConverter
    fun stringToLocalDate(value: String?): LocalDate? {
        return LocalDate.parse(value)
    }
}