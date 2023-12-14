package com.jae464.data.datasource

import com.jae464.data.database.entity.ProgressTaskEntity
import com.jae464.data.database.entity.ProgressTaskWithTask
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ProgressTaskLocalDataSource {
    fun getProgressTask(progressTaskId: String): Flow<ProgressTaskWithTask?>
    fun getTodayProgressTasks(): Flow<List<ProgressTaskWithTask>>
    fun getProgressTasksByDate(startDate: LocalDate, endDate: LocalDate): Flow<List<ProgressTaskWithTask>>
    suspend fun updateProgressTime(progressTaskId: String, progressedTime: Int)
    suspend fun updateTodayMemo(progressTaskId: String, todayMemo: String)
    suspend fun insertProgressTask(progressTaskEntity: ProgressTaskEntity)
}