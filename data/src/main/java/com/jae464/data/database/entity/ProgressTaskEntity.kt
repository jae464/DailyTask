package com.jae464.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jae464.domain.model.ProgressTask
import java.time.LocalDate

@Entity(
    tableName = "progress_tasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProgressTaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "total_time") val totalTime: Int,
    @ColumnInfo(name = "progressed_time") val progressedTime: Int,
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "memo") val memo: String,
    @ColumnInfo(name = "today_memo") val todayMemo: String,
    @ColumnInfo(name = "created_at") val createdAt: LocalDate
)

fun ProgressTask.toEntity(): ProgressTaskEntity {
    return ProgressTaskEntity(
        id = id,
        title = title,
        totalTime = totalTime,
        progressedTime = progressedTime,
        taskId = task.id,
        categoryId = category.id,
        memo = memo,
        todayMemo = "",
        createdAt = createdAt
    )
}
