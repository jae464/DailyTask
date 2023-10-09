package com.jae464.domain.usecase

import com.jae464.domain.model.Task
import com.jae464.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task) = taskRepository.updateTask(task)
}