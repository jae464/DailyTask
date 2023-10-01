package com.jae464.data.datasource

import com.jae464.data.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryLocalDataSource {
    fun getAllCategories(): Flow<List<CategoryEntity>>
    suspend fun insertCategory(categoryEntity: CategoryEntity)
    suspend fun deleteCategory(categoryId: Long)
}