package com.jae464.data.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProgressTaskWithTask(
    @Embedded val progressTaskEntity: ProgressTaskEntity,
    @Relation(
        parentColumn = "task_id",
        entityColumn = "id"
    )
    val task: TaskEntity
)