package com.jae464.domain.model

import java.time.LocalDateTime

data class Task(
    val id: String,
    val title: String,
    val memo: String,
    val progressTime: Int,
    val taskType: TaskType,
    val dayOfWeeks: List<DayOfWeek>,
    val alarmTime: LocalDateTime,
    val categoryId: Long
)

