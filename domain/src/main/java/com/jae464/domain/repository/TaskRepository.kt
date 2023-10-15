package com.jae464.domain.repository

import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTask(taskId: String): Flow<Task>
    fun getProgressTask(progressTaskId: String): Flow<ProgressTask>
    suspend fun saveTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: String)
}