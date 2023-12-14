package com.jae464.presentation.model

import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.Task
import com.jae464.domain.model.TaskType
import java.time.LocalDateTime

data class AddTaskUiModel(
    val title: String,
    val progressTimeHour: Int,
    val progressTimeMinute: Int,
    val taskType: TaskType,
    val dayOfWeeks: List<DayOfWeek>,
    val useAlarm: Boolean,
    val alarmTime: LocalDateTime,
    val memo: String,
    val categoryId: Long
)

fun Task.toAddTaskUiModel(): AddTaskUiModel {
    return AddTaskUiModel(
        title = title,
        progressTimeHour = progressTime / 3600,
        progressTimeMinute = progressTime % 3600 / 60,
        taskType = taskType,
        dayOfWeeks = dayOfWeeks,
        useAlarm = useAlarm,
        alarmTime = alarmTime,
        memo = memo,
        categoryId = categoryId
    )
}

fun AddTaskUiModel.toTask(): Task {
    return Task(
        id = "",
        title = title,
        memo = memo,
        progressTime = progressTimeHour * 3600 + progressTimeMinute * 60,
        taskType = taskType,
        dayOfWeeks = dayOfWeeks,
        useAlarm = useAlarm,
        alarmTime = alarmTime,
        categoryId = categoryId
    )
}
