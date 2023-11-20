package com.jae464.domain.usecase

import com.jae464.domain.model.ProgressTask
import com.jae464.domain.repository.TaskRepository
import kotlinx.coroutines.flow.flowOf
import java.util.concurrent.Flow
import javax.inject.Inject

class GetProgressTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(progressTaskId: String) = taskRepository.getProgressTask(progressTaskId)

}