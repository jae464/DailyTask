package com.jae464.presentation.model

import android.util.Log
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.Task
import com.jae464.domain.model.TaskType

data class TaskUIModel(
    val id: String,
    val title: String,
    val progressTime: Int,
    val taskType: TaskType,
    val dayOfWeek: List<DayOfWeek>?,
    val category: String,
    val alarmHour: Int,
    val alarmMinute: Int,
) {
    val header = taskType.taskName + " / " + category
    val progressTimeStr = "%2d시간 %02d분".format(progressTime / 3600, progressTime % 3600 / 60)
//    val progressTime = "%2d시간 %02d분".format(timeHour, timeMinute)
    val alarmTime = "%2d시 %02d분".format(alarmHour, alarmMinute)
}

fun Task.toTaskUIModel(categoryName: String): TaskUIModel {
    return TaskUIModel(
        id = id,
        title = title,
        progressTime = progressTime,
        taskType = taskType,
        dayOfWeek = dayOfWeeks,
        category = categoryName,
        alarmHour = alarmTime.hour,
        alarmMinute = alarmTime.minute
    )
}


