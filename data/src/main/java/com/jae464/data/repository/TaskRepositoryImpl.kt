package com.jae464.data.repository

import com.jae464.data.database.entity.toDomain
import com.jae464.data.database.entity.toEntity
import com.jae464.data.datasource.TaskLocalDataSource
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.SortBy
import com.jae464.domain.model.Task
import com.jae464.domain.model.TaskType
import com.jae464.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
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

    override fun getTasksByTitle(title: String): Flow<List<Task>> {
        return taskLocalDataSource.getTasksByTitle(title).map { taskEntities ->
            taskEntities.map { it.toDomain() }
        }
    }

    override fun getTasksByDayOfWeek(dayOfWeeks: DayOfWeek): Flow<List<Task>> {
        return taskLocalDataSource.getTasksByDayOfWeek(dayOfWeeks).map { taskEntities ->
            taskEntities.map {
                it.toDomain()
            }
        }
    }

    override fun getFilteredTasks(
        usePeriod: Boolean,
        startDate: LocalDate,
        endDate: LocalDate,
        useFilterCategory: Boolean,
        filterCategoryIds: Set<Long>,
        useFilterTaskType: Boolean,
        filterTaskType: TaskType,
        useFilterDayOfWeeks: Boolean,
        filterDayOfWeeks: Set<DayOfWeek>,
        sortBy: SortBy
    ): Flow<List<Task>> {
        return taskLocalDataSource.getFilteredTasks(
            usePeriod, startDate, endDate, useFilterCategory, filterCategoryIds, useFilterTaskType, filterTaskType, useFilterDayOfWeeks, filterDayOfWeeks, sortBy
        ).map { taskEntities ->
            taskEntities.map { it.toDomain() }
        }
    }

    override suspend fun saveTask(task: Task) {
        taskLocalDataSource.insertTask(task.toEntity())
    }


    override suspend fun updateTask(task: Task) {
        taskLocalDataSource.updateTask(task.toEntity(task.id))
    }

    override suspend fun deleteTask(taskId: String) {
        taskLocalDataSource.deleteTask(taskId)
    }
}