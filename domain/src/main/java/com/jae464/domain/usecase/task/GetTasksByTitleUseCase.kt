package com.jae464.domain.usecase.task

import com.jae464.domain.repository.TaskRepository
import javax.inject.Inject

class GetTasksByTitleUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(title: String) = taskRepository.getTasksByTitle(title)
}