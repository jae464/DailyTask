package com.jae464.presentation.model

import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.HourMinute
import com.jae464.domain.model.Task
import com.jae464.domain.model.TaskType
import java.time.LocalDateTime
import java.time.LocalTime

data class AddTaskUIModel(
    val title: String,
    val progressTime: HourMinute,
    val taskType: TaskType,
    val dayOfWeeks: List<DayOfWeek>,
    val alarmTime: LocalDateTime,
    val memo: String,
    val categoryId: Long
)

fun Task.toAddTaskUiModel(): AddTaskUIModel {
    return AddTaskUIModel(
        title = title,
        progressTime = progressTime,
        taskType = taskType,
        dayOfWeeks = dayOfWeeks,
        alarmTime = alarmTime,
        memo = memo,
        categoryId = categoryId
    )
}

fun AddTaskUIModel.toTask(): Task {
    return Task(
        id = "",
        title = title,
        memo = memo,
        progressTime = progressTime,
        taskType = taskType,
        dayOfWeeks = dayOfWeeks,
        alarmTime = alarmTime,
        categoryId = categoryId
    )
}
