package com.jae464.data.repository

import com.jae464.data.database.entity.toDomain
import com.jae464.data.database.entity.toEntity
import com.jae464.data.datasource.CategoryLocalDataSource
import com.jae464.domain.model.Category
import com.jae464.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryLocalDataSource: CategoryLocalDataSource
) : CategoryRepository {
    override fun getAllCategories(): Flow<List<Category>> {
        return categoryLocalDataSource.getAllCategories().map { categoryEntities ->
            categoryEntities.map {
                it.toDomain()
            }
        }
    }

    override fun getCategory(categoryId: Long): Flow<Category> {
        return categoryLocalDataSource.getCategory(categoryId).map { categoryEntity ->
            categoryEntity.toDomain()
        }
    }

    override suspend fun addCategory(category: Category) {
        categoryLocalDataSource.insertCategory(
            category.toEntity()
        )
    }

    override suspend fun deleteCategory(categoryId: Long) {
        categoryLocalDataSource.deleteCategory(categoryId)
    }

    override suspend fun editCategory(categoryId: Long, categoryName: String) {
        categoryLocalDataSource.editCategoryName(categoryId, categoryName)
    }

}