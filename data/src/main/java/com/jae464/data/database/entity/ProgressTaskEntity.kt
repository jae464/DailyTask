package com.jae464.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jae464.domain.model.HourMinute
import com.jae464.domain.model.ProgressTask
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "progress_tasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"]
        )
    ]
)
data class ProgressTaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "total_time") val totalTime: HourMinute,
    @ColumnInfo(name = "progressed_time") val progressedTime: HourMinute,
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "memo") val memo: String,
    @ColumnInfo(name = "created_at") val createdAt: LocalDate
)

fun ProgressTaskEntity.toDomain(): ProgressTask {
    return ProgressTask(
        id = id,
        totalTime = totalTime,
        progressedTime = progressedTime,
        taskId = taskId,
        memo = memo,
        createdAt = createdAt
    )
}

fun ProgressTask.toEntity(): ProgressTaskEntity {
    return ProgressTaskEntity(
        id = id,
        totalTime = totalTime,
        progressedTime = progressedTime,
        taskId = taskId,
        memo = memo,
        createdAt = createdAt
    )
}
