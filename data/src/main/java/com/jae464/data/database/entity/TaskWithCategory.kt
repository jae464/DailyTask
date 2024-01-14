package com.jae464.data.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.jae464.domain.model.Task

data class TaskWithCategory(
    @Embedded val taskEntity: TaskEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val categoryEntity: CategoryEntity,
)

fun TaskWithCategory.toDomain(): Task {
    return Task(
        id = taskEntity.id,
        title = taskEntity.title,
        memo = taskEntity.memo,
        progressTime = taskEntity.progressTime,
        taskType = taskEntity.taskType,
        dayOfWeeks = taskEntity.dayOfWeeks,
        useAlarm = taskEntity.useAlarm,
        alarmTime = taskEntity.alarmTime,
        category = categoryEntity.toDomain()
    )
}