package com.jae464.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

data class CategoryEntity(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "category_name")
    val categoryName: String
)
