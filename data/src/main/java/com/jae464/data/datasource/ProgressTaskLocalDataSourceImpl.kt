package com.jae464.data.datasource

import com.jae464.data.database.dao.ProgressTaskDao
import com.jae464.data.database.entity.ProgressTaskEntity
import com.jae464.data.database.entity.ProgressTaskWithTask
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class ProgressTaskLocalDataSourceImpl @Inject constructor(
    private val progressTaskDao: ProgressTaskDao
) : ProgressTaskLocalDataSource {
    override fun getProgressTask(progressTaskId: String): Flow<ProgressTaskWithTask?> {
        return progressTaskDao.getProgressTask(progressTaskId)
    }

    override fun getTodayProgressTasks(): Flow<List<ProgressTaskWithTask>> {
        return progressTaskDao.getProgressTaskByDate(LocalDate.now())
    }


    override fun getProgressTasksByDate(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<ProgressTaskWithTask>> {
        return progressTaskDao.getProgressTasksByDateRange(startDate, endDate)
    }

    override suspend fun updateProgressTime(progressTaskId: String, progressedTime: Int) {
        progressTaskDao.updateProgressedTime(progressTaskId, progressedTime)
    }

    override suspend fun updateTodayMemo(progressTaskId: String, todayMemo: String) {
        progressTaskDao.updateTodayMemo(progressTaskId, todayMemo)
    }

    override suspend fun insertProgressTask(progressTaskEntity: ProgressTaskEntity) {
        progressTaskDao.insertProgressTask(progressTaskEntity)
    }

}