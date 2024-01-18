package com.jae464.data.datasource

import com.jae464.data.database.dao.TaskDao
import com.jae464.data.database.entity.TaskEntity
import com.jae464.data.database.entity.TaskWithCategory
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.SortBy
import com.jae464.domain.model.TaskType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class TaskLocalDataSourceImpl @Inject constructor(
    private val taskDao: TaskDao,
) : TaskLocalDataSource {
    override fun getAllTasks(): Flow<List<TaskWithCategory>> {
        return taskDao.getAllTasks()
    }

    override fun getTask(taskId: String): Flow<TaskWithCategory> {
        return taskDao.getTask(taskId)
    }

    override fun getTasksByTitle(title: String): Flow<List<TaskWithCategory>> {
        val searchQuery = "%$title%"
        return taskDao.getTasksByTitle(searchQuery, title)
    }

    override fun getTasksByDayOfWeek(dayOfWeeks: DayOfWeek): Flow<List<TaskWithCategory>> {
        return taskDao.getTasksByDayOfWeek(dayOfWeeks.day)
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
    ): Flow<List<TaskWithCategory>> {
        return when (sortBy) {
            SortBy.ASC -> {
                taskDao.getFilteredTasks(
                    usePeriod, startDate, endDate, useFilterCategory, filterCategoryIds, useFilterTaskType, filterTaskType
                ).map { taskEntities ->
                    if (useFilterDayOfWeeks) {
                        taskEntities.filter { it.taskEntity.dayOfWeeks.intersect(filterDayOfWeeks).isNotEmpty() }
                    }
                    else taskEntities
                }
            }

            SortBy.DESC -> {
                taskDao.getFilteredTasksOrderByDesc(
                    usePeriod, startDate, endDate, useFilterCategory, filterCategoryIds, useFilterTaskType, filterTaskType
                ).map { taskEntities ->
                    if (useFilterDayOfWeeks) {
                        taskEntities.filter { it.taskEntity.dayOfWeeks.intersect(filterDayOfWeeks).isNotEmpty() }
                    }
                    else taskEntities
                }
            }
        }
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