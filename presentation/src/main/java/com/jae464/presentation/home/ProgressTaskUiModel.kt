package com.jae464.presentation.home

import com.jae464.domain.model.HourMinute
import com.jae464.domain.model.ProgressTask

data class ProgressTaskUiModel(
    val title: String,
    val memo: String,
    val todayMemo: String,
    val categoryName: String,
    val totalTime: HourMinute,
    val progressedTime: HourMinute
)

fun ProgressTask.toProgressTaskUiModel(): ProgressTaskUiModel {
    return ProgressTaskUiModel(
        title = title,
        memo = memo,
        todayMemo = todayMemo,
        categoryName = category.name,
        totalTime = totalTime,
        progressedTime = progressedTime
    )
}
