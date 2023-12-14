package com.jae464.domain.repository

import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ProgressTaskRepository {
    fun getProgressTask(progressTaskId: String): Flow<ProgressTask?>
    fun getTodayProgressTasks(): Flow<List<ProgressTask>>
    fun getProgressTasksByDate(startDate: LocalDate, endDate: LocalDate): Flow<List<ProgressTask>>
    suspend fun updateProgressTask(tasks: List<Task>)
    suspend fun updateProgressedTime(progressTaskId: String, progressedTime: Int)
    suspend fun updateTodayMemo(progressTaskId: String, todayMemo: String)
    suspend fun insertProgressTask(progressTask: ProgressTask)

}