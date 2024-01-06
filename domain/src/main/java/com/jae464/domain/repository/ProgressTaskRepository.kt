package com.jae464.domain.repository

import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.Task
import com.jae464.domain.model.TaskType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ProgressTaskRepository {
    fun getProgressTask(progressTaskId: String): Flow<ProgressTask?>
    fun getTodayProgressTasks(): Flow<List<ProgressTask>>
    fun getProgressTasksByDate(startDate: LocalDate, endDate: LocalDate): Flow<List<ProgressTask>>
    fun getFilteredProgressTasks(
        usePeriod: Boolean = false,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now(),
        useFilterCategory: Boolean = false,
        filterCategoryIds: Set<Long> = emptySet(),
        useFilterTaskType: Boolean = false,
        filterTaskType: TaskType = TaskType.Regular,
        useFilterDayOfWeeks: Boolean = false,
        filterDayOfWeeks: Set<DayOfWeek> = emptySet()
    ): Flow<List<ProgressTask>>
    fun getProgressTasksByTaskId(
        usePeriod: Boolean = false,
        taskId: String,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now()
    ): Flow<List<ProgressTask>>
    suspend fun updateProgressTask(tasks: List<Task>)
    suspend fun updateProgressedTime(progressTaskId: String, progressedTime: Int)
    suspend fun updateTodayMemo(progressTaskId: String, todayMemo: String)
    suspend fun insertProgressTask(progressTask: ProgressTask)
    suspend fun isExistProgressTaskByDate(taskId: String, createdAt: LocalDate): Boolean
}