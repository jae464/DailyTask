package com.jae464.data.datasource

import com.jae464.data.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryLocalDataSource {
    fun getAllCategories(): Flow<List<CategoryEntity>>
    fun getCategory(categoryId: Long): Flow<CategoryEntity>
    suspend fun insertCategory(categoryEntity: CategoryEntity)
    suspend fun deleteCategory(categoryId: Long)
    suspend fun editCategoryName(categoryId: Long, categoryName: String)
}