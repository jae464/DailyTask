package com.jae464.domain.usecase

import com.jae464.domain.model.ProgressTask
import com.jae464.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayProgressTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(): Flow<List<ProgressTask>> = taskRepository.getTodayProgressTasks()
}