package com.jae464.domain.usecase

import com.jae464.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateProgressedTimeUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(progressTaskId: String, progressedTime: Int) =
        taskRepository.updateProgressedTime(progressTaskId, progressedTime)
}