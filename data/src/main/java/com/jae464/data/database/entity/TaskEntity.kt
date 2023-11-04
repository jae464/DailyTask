package com.jae464.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.Task
import com.jae464.domain.model.TaskType
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"]
        )
    ]
)
data class TaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "memo") val memo: String,
    @ColumnInfo(name = "progress_time") val progressTime: Int,
    @ColumnInfo(name = "task_type") val taskType: TaskType,
    @ColumnInfo(name = "day_of_week") val dayOfWeeks: List<DayOfWeek>,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "alarm_time") val alarmTime: LocalDateTime,
    @ColumnInfo(name = "created_at") val createdAt: LocalDateTime
)

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        memo = memo,
        progressTime = progressTime,
        taskType = taskType,
        dayOfWeeks = dayOfWeeks,
        alarmTime = alarmTime,
        categoryId = categoryId
    )
}

fun Task.toEntity(taskId: String? = null): TaskEntity {
    return TaskEntity(
        id = taskId ?: UUID.randomUUID().toString(),
        title = title,
        memo = memo,
        progressTime = progressTime,
        taskType = taskType,
        dayOfWeeks = dayOfWeeks,
        categoryId = categoryId,
        alarmTime = alarmTime,
        createdAt = LocalDateTime.now()
    )
}

fun Task.toProgressTaskEntity(): ProgressTaskEntity {
    return ProgressTaskEntity(
        id = UUID.randomUUID().toString(),
        title = title,
        totalTime = progressTime,
        progressedTime = 0,
        taskId = id,
        categoryId = categoryId,
        memo = memo,
        todayMemo = "",
        createdAt = LocalDate.now()
    )
}