package com.jae464.data.repository

import com.jae464.data.database.entity.toDomain
import com.jae464.data.database.entity.toEntity
import com.jae464.data.datasource.TaskLocalDataSource
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.Task
import com.jae464.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskLocalDataSource: TaskLocalDataSource,
): TaskRepository {
    override fun getAllTasks(): Flow<List<Task>> {
        return taskLocalDataSource.getAllTasks()
            .map {taskEntities ->
            taskEntities.map {
                it.toDomain()
            }
        }
    }

    override fun getTask(taskId: String): Flow<Task> {
        return taskLocalDataSource.getTask(taskId).map {taskEntity ->
            taskEntity.toDomain()
        }
    }

    override fun getProgressTask(progressTaskId: String): Flow<ProgressTask> {
        return taskLocalDataSource.getProgressTask(progressTaskId).map { progressTaskEntity ->
            progressTaskEntity.toDomain()
        }
    }

    override suspend fun saveTask(task: Task) {
        taskLocalDataSource.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskLocalDataSource.insertTask(task.toEntity(task.id))
    }

    override suspend fun deleteTask(taskId: String) {
        taskLocalDataSource.deleteTask(taskId)
    }
}