package com.jae464.domain.model

import java.time.LocalDateTime

data class Task(
    val id: String,
    val title: String,
    val progressTime: HourMinute,
    val taskType: TaskType,
    val dayOfWeeks: List<DayOfWeek>,
    val alarmTime: LocalDateTime,
    val categoryId: Long
)

