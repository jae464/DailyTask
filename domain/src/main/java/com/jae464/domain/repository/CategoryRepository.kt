package com.jae464.domain.repository

import com.jae464.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    fun getCategory(categoryId: Long): Flow<Category>
    suspend fun addCategory(category: Category)
    suspend fun deleteCategory(categoryId: Long)
    suspend fun editCategory(categoryId: Long, categoryName: String)
    suspend fun changeDefaultCategory(categoryId: Long)
}