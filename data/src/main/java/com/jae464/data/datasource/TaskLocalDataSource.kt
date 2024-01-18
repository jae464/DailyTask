package com.jae464.data.datasource

import com.jae464.data.database.entity.ProgressTaskEntity
import com.jae464.data.database.entity.ProgressTaskWithTask
import com.jae464.data.database.entity.TaskEntity
import com.jae464.data.database.entity.TaskWithCategory
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.SortBy
import com.jae464.domain.model.Task
import com.jae464.domain.model.TaskType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TaskLocalDataSource {
    fun getAllTasks(): Flow<List<TaskWithCategory>>
    fun getTask(taskId: String): Flow<TaskWithCategory>
    fun getTasksByTitle(title: String): Flow<List<TaskWithCategory>>
    fun getTasksByDayOfWeek(dayOfWeeks: DayOfWeek): Flow<List<TaskWithCategory>>
    fun getFilteredTasks(
        usePeriod: Boolean = false,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now(),
        useFilterCategory: Boolean = false,
        filterCategoryIds: Set<Long> = emptySet(),
        useFilterTaskType: Boolean = false,
        filterTaskType: TaskType = TaskType.Regular,
        useFilterDayOfWeeks: Boolean = false,
        filterDayOfWeeks: Set<DayOfWeek> = emptySet(),
        sortBy: SortBy = SortBy.ASC
    ): Flow<List<TaskWithCategory>>
    suspend fun insertTask(taskEntity: TaskEntity)
    suspend fun updateTask(taskEntity: TaskEntity)
    suspend fun deleteTask(taskId: String)
}