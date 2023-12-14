package com.jae464.domain.usecase.progresstask

import com.jae464.domain.repository.ProgressTaskRepository
import com.jae464.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTodayMemoUseCase @Inject constructor(
    private val progressTaskRepository: ProgressTaskRepository
) {
    suspend operator fun invoke(progressTaskId: String, todayMemo: String) = progressTaskRepository.updateTodayMemo(progressTaskId, todayMemo)
}