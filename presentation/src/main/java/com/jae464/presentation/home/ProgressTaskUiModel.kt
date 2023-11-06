package com.jae464.presentation.home

import com.jae464.domain.model.ProgressTask
import com.jae464.presentation.model.HourMinuteSecond

data class ProgressTaskUiModel(
    val id: String,
    val title: String,
    val memo: String,
    val todayMemo: String,
    val categoryName: String,
    val totalTime: Int,
    val progressedTime: Int,
    val isProgressing: Boolean
) {
    val remainTime = totalTime - progressedTime
}

fun ProgressTask.toProgressTaskUiModel(isProgressing: Boolean = false): ProgressTaskUiModel {
    return ProgressTaskUiModel(
        id = id,
        title = title,
        memo = memo,
        todayMemo = todayMemo,
        categoryName = category.name,
        totalTime = totalTime,
        progressedTime = progressedTime,
        isProgressing = isProgressing
    )
}