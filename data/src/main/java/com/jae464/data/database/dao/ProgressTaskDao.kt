package com.jae464.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jae464.data.database.entity.ProgressTaskEntity
import com.jae464.data.database.entity.ProgressTaskWithTask
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.TaskType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ProgressTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressTask(progressTaskEntity: ProgressTaskEntity)

    @Query("SELECT * FROM progress_tasks")
    fun getAllProgressTasks(): Flow<List<ProgressTaskEntity>>

    @Transaction
    @Query("SELECT * FROM progress_tasks  WHERE id = :progressTaskId")
    fun getProgressTask(progressTaskId: String): Flow<ProgressTaskWithTask?>

    @Transaction
    @Query("SELECT * FROM progress_tasks WHERE created_at = :date")
    fun getProgressTaskByDate(date: LocalDate): Flow<List<ProgressTaskWithTask>>

    @Transaction
    @Query(
        value = """
            SELECT * FROM progress_tasks
            WHERE
                CASE WHEN :usePeriod
                    THEN created_at BETWEEN :startDate AND :endDate
                    ELSE 1
                END
            AND
                CASE WHEN :useFilterCategory
                    THEN task_id IN
                        (
                            SELECT id FROM tasks
                            WHERE category_id IN (:filterCategoryIds)
                        )
                    ELSE 1
                END
            AND
                CASE WHEN :useFilterTaskType
                    THEN task_id IN
                        (
                            SELECT id FROM tasks
                            WHERE task_type = :filterTaskType
                        )
                    ELSE 1
                END
        """
    )
    fun getFilteredProgressTasks(
        usePeriod: Boolean = false,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now(),
        useFilterCategory: Boolean = false,
        filterCategoryIds: Set<Long> = emptySet(),
        useFilterTaskType: Boolean = false,
        filterTaskType: TaskType = TaskType.Regular,
    ): Flow<List<ProgressTaskWithTask>>

    @Transaction
    @Query("SELECT * FROM progress_tasks WHERE created_at BETWEEN :startDate AND :endDate")
    fun getProgressTasksByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<ProgressTaskWithTask>>


    @Query("UPDATE progress_tasks SET progressed_time = :progressedTime WHERE id = :progressTaskId")
    suspend fun updateProgressedTime(progressTaskId: String, progressedTime: Int)

    @Query("UPDATE progress_tasks SET today_memo = :todayMemo WHERE id = :progressTaskId")
    suspend fun updateTodayMemo(progressTaskId: String, todayMemo: String)

}