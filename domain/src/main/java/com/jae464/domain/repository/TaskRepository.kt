package com.jae464.domain.repository

import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.Task
import com.jae464.domain.model.TaskType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTask(taskId: String): Flow<Task>
    fun getTasksByDayOfWeek(dayOfWeeks: DayOfWeek): Flow<List<Task>>
    fun getFilteredTasks(
        usePeriod: Boolean = false,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now(),
        useFilterCategory: Boolean = false,
        filterCategoryIds: Set<Long> = emptySet(),
        useFilterTaskType: Boolean = false,
        filterTaskType: TaskType = TaskType.Regular,
    ): Flow<List<Task>>
    suspend fun saveTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: String)
}