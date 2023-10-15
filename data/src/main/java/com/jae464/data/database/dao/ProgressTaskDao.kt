package com.jae464.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jae464.data.database.entity.ProgressTaskEntity
import com.jae464.domain.model.HourMinute
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressTask(progressTaskEntity: ProgressTaskEntity)

    @Query("SELECT * FROM progress_tasks")
    fun getAllProgressTasks(): Flow<List<ProgressTaskEntity>>

    @Query("SELECT * FROM progress_tasks WHERE id = :progressTaskId")
    fun getProgressTask(progressTaskId: String): Flow<ProgressTaskEntity>

    @Query("UPDATE progress_tasks SET progressed_time = :progressedTime WHERE id = :progressTaskId")
    fun updateProgressedTime(progressTaskId: String, progressedTime: HourMinute)

}