package com.jae464.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jae464.data.database.entity.TaskEntity
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.HourMinute
import com.jae464.domain.model.TaskType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(taskEntity: TaskEntity)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTask(taskId: String): Flow<TaskEntity>

    @Query("SELECT * FROM tasks WHERE day_of_week LIKE '%' || :dayOfWeek || '%'")
    fun getTasksByDayOfWeek(dayOfWeek: String): Flow<List<TaskEntity>>

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