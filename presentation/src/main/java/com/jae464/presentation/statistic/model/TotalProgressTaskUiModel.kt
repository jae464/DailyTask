package com.jae464.presentation.statistic.model

import com.jae464.domain.model.Category
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.Task

data class TotalProgressTaskUiModel(
    val title: String,
    val totalTime: Int,
    val totalProgressedTime: Int,
    val task: Task,
    val category: Category
) {
    val totalProgressedTimeStr = "%2d시간 %02d분".format(totalProgressedTime / 3600, totalProgressedTime % 3600 / 60)
}

fun List<ProgressTask>.toTotalProgressTaskUiModels(): List<TotalProgressTaskUiModel> {
    val group = this.groupBy { it.title }
    val totalProgressTasks = group.entries.map { tasks ->
        val totalTime = tasks.value.sumOf { it.totalTime }
        val totalProgressedTime = tasks.value.sumOf { it.progressedTime }
        val title = tasks.value.first().title
        val task = tasks.value.first().task
        val category = tasks.value.first().category

        TotalProgressTaskUiModel(
            title = title,
            totalTime = totalTime,
            totalProgressedTime = totalProgressedTime,
            task = task,
            category = category
        )
    }
    return totalProgressTasks
}
