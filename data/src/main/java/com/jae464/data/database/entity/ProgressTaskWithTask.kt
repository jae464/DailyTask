package com.jae464.data.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.jae464.domain.model.ProgressTask

data class ProgressTaskWithTask(
    @Embedded val progressTaskEntity: ProgressTaskEntity,
    @Relation(
        parentColumn = "task_id",
        entityColumn = "id"
    )
    val task: TaskEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: CategoryEntity
)

fun ProgressTaskWithTask.toDomain(): ProgressTask {
    return ProgressTask(
        id = progressTaskEntity.id,
        title = progressTaskEntity.title,
        totalTime = progressTaskEntity.totalTime,
        progressedTime = progressTaskEntity.progressedTime,
        task = task.toDomain(),
        category = category.toDomain(),
        memo = progressTaskEntity.memo,
        todayMemo = progressTaskEntity.todayMemo,
        createdAt = progressTaskEntity.createdAt
    )
}
