package com.jae464.data.datasource

import com.jae464.data.database.dao.CategoryDao
import com.jae464.data.database.dao.ProgressTaskDao
import com.jae464.data.database.dao.TaskDao
import com.jae464.data.database.entity.ProgressTaskEntity
import com.jae464.data.database.entity.ProgressTaskWithTask
import com.jae464.data.database.entity.TaskEntity
import com.jae464.domain.model.DayOfWeek
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class TaskLocalDataSourceImpl @Inject constructor(
    private val taskDao: TaskDao,
) : TaskLocalDataSource {
    override fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }

    override fun getTask(taskId: String): Flow<TaskEntity> {
        return taskDao.getTask(taskId)
    }

    override fun getTasksByDayOfWeek(dayOfWeeks: DayOfWeek): Flow<List<TaskEntity>> {
        return taskDao.getTasksByDayOfWeek(dayOfWeeks.day)
    }

    override suspend fun insertTask(taskEntity: TaskEntity) {
        taskDao.insertTask(taskEntity)
    }

    override suspend fun updateTask(taskEntity: TaskEntity) {
        taskDao.updateTask(taskEntity)
    }

    override suspend fun deleteTask(taskId: String) {
        taskDao.deleteTask(taskId)
    }
}