package com.jae464.data.datasource

import com.jae464.data.database.dao.CategoryDao
import com.jae464.data.database.dao.TaskDao
import com.jae464.data.database.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskLocalDataSourceImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val categoryDao: CategoryDao
) : TaskLocalDataSource {
    override fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }

    override suspend fun insertTask(taskEntity: TaskEntity) {
        taskDao.insertTask(taskEntity)
    }

    override suspend fun deleteTask(taskId: String) {
        taskDao.deleteTask(taskId)
    }
}