package com.jae464.domain.usecase

import com.jae464.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTodayMemoUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(progressTaskId: String, todayMemo: String) = taskRepository.updateTodayMemo(progressTaskId, todayMemo)
}