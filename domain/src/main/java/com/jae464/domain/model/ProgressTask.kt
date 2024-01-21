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

fun Task.toProgressTask(
    progressedTime: Int = 0,
    createdAt: LocalDate = LocalDate.now()
): ProgressTask {
    return ProgressTask(
        id = id,
        title = title,
        totalTime = progressTime,
        progressedTime = progressedTime,
        task = this,
        category = category,
        memo = memo,
        todayMemo = "",
        createdAt = createdAt
    )
}
