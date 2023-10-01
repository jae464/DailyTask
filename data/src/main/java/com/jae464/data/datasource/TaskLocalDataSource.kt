package com.jae464.data.datasource

import com.jae464.data.database.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskLocalDataSource {
    fun getAllTasks(): Flow<List<TaskEntity>>
    suspend fun insertTask(taskEntity: TaskEntity)
    suspend fun deleteTask(taskId: String)
}