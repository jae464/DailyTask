package com.jae464.domain.repository

import com.jae464.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun saveTask(task: Task)
    suspend fun deleteTask(taskId: String)
}