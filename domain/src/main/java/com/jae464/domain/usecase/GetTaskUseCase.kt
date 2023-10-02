package com.jae464.domain.usecase

import com.jae464.domain.model.Task
import com.jae464.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(taskId: String): Flow<Task> = taskRepository.getTask(taskId)
}