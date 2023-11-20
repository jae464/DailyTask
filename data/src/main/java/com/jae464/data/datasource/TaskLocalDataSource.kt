package com.jae464.data.datasource

import com.jae464.data.database.entity.ProgressTaskEntity
import com.jae464.data.database.entity.ProgressTaskWithTask
import com.jae464.data.database.entity.TaskEntity
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TaskLocalDataSource {
    fun getAllTasks(): Flow<List<TaskEntity>>
    fun getTask(taskId: String): Flow<TaskEntity>
    fun getTasksByDayOfWeek(dayOfWeeks: DayOfWeek): Flow<List<TaskEntity>>
    fun getProgressTask(progressTaskId: String): Flow<ProgressTaskWithTask?>
    fun getTodayProgressTasks(): Flow<List<ProgressTaskWithTask>>
    fun getProgressTasksByDate(startDate: LocalDate, endDate: LocalDate): Flow<List<ProgressTaskWithTask>>
    suspend fun updateProgressTime(progressTaskId: String, progressedTime: Int)
    suspend fun insertTask(taskEntity: TaskEntity)
    suspend fun insertProgressTask(progressTaskEntity: ProgressTaskEntity)
    suspend fun updateTask(taskEntity: TaskEntity)
    suspend fun deleteTask(taskId: String)
}