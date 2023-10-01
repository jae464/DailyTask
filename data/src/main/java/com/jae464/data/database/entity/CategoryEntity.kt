package com.jae464.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jae464.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "category_name")
    val categoryName: String
)

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = categoryName
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        categoryName = name
    )
}