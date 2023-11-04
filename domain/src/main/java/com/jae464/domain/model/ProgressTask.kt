package com.jae464.domain.model

import java.time.LocalDate

data class ProgressTask(
    val id: String,
    val title: String,
    val totalTime: Int,
    val progressedTime: Int,
    val task: Task,
    val category: Category,
    val memo: String,
    val todayMemo: String,
    val createdAt: LocalDate
)