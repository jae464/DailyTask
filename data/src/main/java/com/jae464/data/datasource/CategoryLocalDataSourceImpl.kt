package com.jae464.data.datasource

import com.jae464.data.database.dao.CategoryDao
import com.jae464.data.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryLocalDataSourceImpl @Inject constructor(
    private val categoryDao: CategoryDao
): CategoryLocalDataSource {
    override fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    override fun getCategory(categoryId: Long): Flow<CategoryEntity> {
        return categoryDao.getCategory(categoryId)
    }

    override suspend fun insertCategory(categoryEntity: CategoryEntity) {
        categoryDao.insertCategory(categoryEntity)
    }

    override suspend fun deleteCategory(categoryId: Long) {
        categoryDao.deleteCategory(categoryId)
    }

    override suspend fun editCategoryName(categoryId: Long, categoryName: String) {
        categoryDao.updateCategoryName(categoryId, categoryName)
    }
}