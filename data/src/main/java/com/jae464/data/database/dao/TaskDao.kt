package com.jae464.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.jae464.data.database.entity.TaskEntity
import com.jae464.data.database.entity.TaskWithCategory
import com.jae464.domain.model.SortBy
import com.jae464.domain.model.TaskType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(taskEntity: TaskEntity)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskWithCategory>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTask(taskId: String): Flow<TaskWithCategory>

    @Transaction
    @Query(
        """
            SELECT * FROM tasks
            WHERE
                CASE WHEN :usePeriod
                    THEN created_at BETWEEN :startDate AND :endDate
                    ELSE 1
                END
            AND
                CASE WHEN :useFilterCategory
                    THEN category_id IN (:filterCategoryIds)
                    ELSE 1
                END
            AND
                CASE WHEN :useFilterTaskType
                    THEN task_type = :filterTaskType
                    ELSE 1
                END
        """
    )
    fun getFilteredTasks(
        usePeriod: Boolean = false,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now(),
        useFilterCategory: Boolean = false,
        filterCategoryIds: Set<Long> = emptySet(),
        useFilterTaskType: Boolean = false,
        filterTaskType: TaskType = TaskType.Regular,
    ): Flow<List<TaskWithCategory>>

    @Query("SELECT * FROM tasks WHERE day_of_week LIKE '%' || :dayOfWeek || '%'")
    fun getTasksByDayOfWeek(dayOfWeek: String): Flow<List<TaskWithCategory>>

    @Update
    suspend fun updateTask(taskEntity: TaskEntity)

    @Query(
        value = """
            DELETE FROM tasks
            WHERE id = :taskId
        """
    )
    suspend fun deleteTask(taskId: String)
}