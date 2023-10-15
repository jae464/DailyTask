package com.jae464.data.datasource

import com.jae464.data.database.entity.ProgressTaskEntity
import com.jae464.data.database.entity.TaskEntity
import com.jae464.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskLocalDataSource {
    fun getAllTasks(): Flow<List<TaskEntity>>
    fun getTask(taskId: String): Flow<TaskEntity>
    fun getProgressTask(progressTaskId: String): Flow<ProgressTaskEntity>
    suspend fun insertTask(taskEntity: TaskEntity)
    suspend fun updateTask(taskEntity: TaskEntity)
    suspend fun deleteTask(taskId: String)
}