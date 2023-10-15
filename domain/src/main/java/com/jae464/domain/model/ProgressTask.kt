package com.jae464.domain.model

import java.time.LocalDateTime

data class ProgressTask(
    val id: String,
    val totalTime: HourMinute,
    val progressedTime: HourMinute,
    val taskId: String,
    val memo: String,
    val createdAt: LocalDateTime
)