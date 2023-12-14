package com.jae464.data.repository

import com.jae464.data.database.entity.toDomain
import com.jae464.data.database.entity.toEntity
import com.jae464.data.database.entity.toProgressTaskEntity
import com.jae464.data.datasource.ProgressTaskLocalDataSource
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.model.Task
import com.jae464.domain.repository.ProgressTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class ProgressTaskRepositoryImpl @Inject constructor(
    private val progressTaskLocalDataSource: ProgressTaskLocalDataSource
): ProgressTaskRepository {
    override fun getProgressTask(progressTaskId: String): Flow<ProgressTask?> {
        return progressTaskLocalDataSource.getProgressTask(progressTaskId).map { progressTaskEntity ->
            progressTaskEntity?.toDomain()
        }
    }

    override fun getTodayProgressTasks(): Flow<List<ProgressTask>> {
        return progressTaskLocalDataSource.getTodayProgressTasks().map { progressTaskEntities ->
            progressTaskEntities.map {
                it.toDomain()
            }
        }
    }

    override fun getProgressTasksByDate(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<ProgressTask>> {
        return progressTaskLocalDataSource.getProgressTasksByDate(startDate, endDate).map { progressTaskEntities ->
            progressTaskEntities.map {
                it.toDomain()
            }
        }
    }

    override suspend fun updateProgressTask(tasks: List<Task>) {
        tasks.forEach {
            progressTaskLocalDataSource.insertProgressTask(it.toProgressTaskEntity())
        }
    }

    override suspend fun updateProgressedTime(progressTaskId: String, progressedTime: Int) {
        progressTaskLocalDataSource.updateProgressTime(progressTaskId, progressedTime)
    }

    override suspend fun updateTodayMemo(progressTaskId: String, todayMemo: String) {
        progressTaskLocalDataSource.updateTodayMemo(progressTaskId, todayMemo)
    }


    override suspend fun insertProgressTask(progressTask: ProgressTask) {
        progressTaskLocalDataSource.insertProgressTask(progressTask.toEntity())
    }

}