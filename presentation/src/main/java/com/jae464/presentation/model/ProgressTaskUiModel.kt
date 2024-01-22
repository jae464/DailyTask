package com.jae464.presentation.model

import com.jae464.domain.model.ProgressTask
import kotlin.math.absoluteValue

data class ProgressTaskUiModel(
    val id: String,
    val title: String,
    val memo: String,
    val todayMemo: String,
    val taskType: String,
    val categoryName: String,
    val totalTime: Int,
    val progressedTime: Int,
    val isProgressing: Boolean
) {
    private val remainTime = totalTime - progressedTime

    fun getRemainTimeString(): String {
        if (remainTime < 0) return getOverTimeString()
        val hour = remainTime / 3600
        val minute = remainTime % 3600 / 60
        val second = remainTime % 3600 % 60
        return "%2d:%02d:%02d".format(hour, minute, second)
    }

    private fun getOverTimeString(): String {
        val overTime = remainTime.absoluteValue
        val hour = overTime / 3600
        val minute = overTime % 3600 / 60
        val second = overTime % 3600 % 60
        return "%2d:%02d:%02d".format(hour, minute, second)
    }

    fun isOverTime(): Boolean {
        return remainTime < 0
    }
}

fun ProgressTask.toProgressTaskUiModel(isProgressing: Boolean = false): ProgressTaskUiModel {
    return ProgressTaskUiModel(
        id = id,
        title = title,
        memo = memo,
        todayMemo = todayMemo,
        taskType = task.taskType.taskName,
        categoryName = category.name,
        totalTime = totalTime,
        progressedTime = progressedTime,
        isProgressing = isProgressing
    )
}
