package com.jae464.domain.usecase.progresstask

import com.jae464.domain.model.Task
import com.jae464.domain.repository.ProgressTaskRepository
import com.jae464.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTodayProgressTasksUseCase @Inject constructor(
    private val progressTaskRepository: ProgressTaskRepository
) {
    suspend operator fun invoke(tasks: List<Task>) = progressTaskRepository.updateProgressTask(tasks)
}