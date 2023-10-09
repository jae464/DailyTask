package com.jae464.domain.usecase

import com.jae464.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String) = taskRepository.deleteTask(taskId)
}