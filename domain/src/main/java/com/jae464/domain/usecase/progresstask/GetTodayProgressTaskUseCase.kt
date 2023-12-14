package com.jae464.domain.usecase.progresstask

import com.jae464.domain.model.ProgressTask
import com.jae464.domain.repository.ProgressTaskRepository
import com.jae464.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayProgressTaskUseCase @Inject constructor(
    private val progressTaskRepository: ProgressTaskRepository
) {
    operator fun invoke(): Flow<List<ProgressTask>> = progressTaskRepository.getTodayProgressTasks()
}