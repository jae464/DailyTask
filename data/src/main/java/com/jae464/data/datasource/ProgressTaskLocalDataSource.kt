package com.jae464.data.datasource

import com.jae464.data.database.entity.ProgressTaskEntity
import com.jae464.data.database.entity.ProgressTaskWithTask
import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.TaskType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ProgressTaskLocalDataSource {
    fun getProgressTask(progressTaskId: String): Flow<ProgressTaskWithTask?>
    fun getTodayProgressTasks(): Flow<List<ProgressTaskWithTask>>
    fun getProgressTasksByDate(startDate: LocalDate, endDate: LocalDate): Flow<List<ProgressTaskWithTask>>
    fun getFilteredProgressTasks(
        usePeriod: Boolean = false,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now(),
        useFilterCategory: Boolean = false,
        filterCategoryIds: Set<Long> = emptySet(),
        useFilterTaskType: Boolean = false,
        filterTaskType: TaskType = TaskType.Regular,
    ): Flow<List<ProgressTaskWithTask>>
    fun getProgressTasksByTaskId(
        usePeriod: Boolean = false,
        taskId: String,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now()
    ): Flow<List<ProgressTaskWithTask>>
    suspend fun updateProgressTime(progressTaskId: String, progressedTime: Int)
    suspend fun updateTodayMemo(progressTaskId: String, todayMemo: String)
    suspend fun insertProgressTask(progressTaskEntity: ProgressTaskEntity)
}