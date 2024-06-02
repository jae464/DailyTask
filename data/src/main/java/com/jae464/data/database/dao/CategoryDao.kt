package com.jae464.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jae464.data.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categoryEntity: CategoryEntity)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategory(categoryId: Long): Flow<CategoryEntity>

    @Query(
        value = """
            DELETE FROM categories
            WHERE id = :categoryId
        """
    )
    suspend fun deleteCategory(categoryId: Long)

    @Query(
        value = """
            UPDATE categories
            SET category_name = :categoryName
            WHERE id = :categoryId 
        """
    )
    suspend fun updateCategoryName(categoryId: Long, categoryName: String)

    @Query("SELECT id FROM categories WHERE is_default = 1")
    suspend fun getDefaultCategoryId(): Long

    @Query("""
        UPDATE categories
        SET is_default = :isDefault
        WHERE id = :categoryId
    """)
    suspend fun updateDefaultCategory(categoryId: Long, isDefault: Boolean)
}