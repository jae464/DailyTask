package com.jae464.domain.model

import java.time.LocalDate

data class ProgressTask(
    val id: String,
    val totalTime: HourMinute,
    val progressedTime: HourMinute,
    val task: Task,
    val category: Category,
    val memo: String,
    val createdAt: LocalDate
)