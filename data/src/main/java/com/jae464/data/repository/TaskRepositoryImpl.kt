package com.jae464.data.repository

import com.jae464.data.database.entity.toDomain
import com.jae464.data.database.entity.toEntity
import com.jae464.data.database.entity.toProgressTaskEntity
import com.jae464.data.datasource.TaskLocalDataSource
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.Task
import com.jae464.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskLocalDataSource: TaskLocalDataSource,
) : TaskRepository {
    override fun getAllTasks(): Flow<List<Task>> {
        return taskLocalDataSource.getAllTasks()
            .map { taskEntities ->
                taskEntities.map {
                    it.toDomain()
                }
            }
    }

    override fun getTask(taskId: String): Flow<Task> {
        return taskLocalDataSource.getTask(taskId).map { taskEntity ->
            taskEntity.toDomain()
        }
    }

    override fun getTasksByDayOfWeek(dayOfWeeks: DayOfWeek): Flow<List<Task>> {
        return taskLocalDataSource.getTasksByDayOfWeek(dayOfWeeks).map { taskEntities ->
            taskEntities.map {
                it.toDomain()
            }
        }
    }


    override fun getProgressTask(progressTaskId: String): Flow<ProgressTask?> {
        return taskLocalDataSource.getProgressTask(progressTaskId).map { progressTaskEntity ->
            progressTaskEntity?.toDomain()
        }
    }

    override fun getTodayProgressTasks(): Flow<List<ProgressTask>> {
        return taskLocalDataSource.getTodayProgressTasks().map { progressTaskEntities ->
            progressTaskEntities.map {
                it.toDomain()
            }
        }
    }

    override fun getProgressTasksByDate(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<ProgressTask>> {
        return taskLocalDataSource.getProgressTasksByDate(startDate, endDate).map { progressTaskEntities ->
            progressTaskEntities.map {
                it.toDomain()
            }
        }
    }

    override suspend fun updateProgressTask(tasks: List<Task>) {
        tasks.forEach {
            taskLocalDataSource.insertProgressTask(it.toProgressTaskEntity())
        }
    }

    override suspend fun updateProgressedTime(progressTaskId: String, progressedTime: Int) {
        taskLocalDataSource.updateProgressTime(progressTaskId, progressedTime)
    }

    override suspend fun updateTodayMemo(progressTaskId: String, todayMemo: String) {
        taskLocalDataSource.updateTodayMemo(progressTaskId, todayMemo)
    }

    override suspend fun saveTask(task: Task) {
        taskLocalDataSource.insertTask(task.toEntity())
    }

    override suspend fun insertProgressTask(progressTask: ProgressTask) {
        taskLocalDataSource.insertProgressTask(progressTask.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskLocalDataSource.updateTask(task.toEntity(task.id))
    }

    override suspend fun deleteTask(taskId: String) {
        taskLocalDataSource.deleteTask(taskId)
    }
}