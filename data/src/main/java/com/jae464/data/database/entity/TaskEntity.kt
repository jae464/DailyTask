package com.jae464.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jae464.domain.model.HourMinute
import java.time.LocalDateTime

@Entity
data class TaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "progress_time") val progressTime: HourMinute,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "created_at") val createdAt: LocalDateTime
)
