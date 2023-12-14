package com.jae464.domain.usecase.progresstask

import com.jae464.domain.model.ProgressTask
import com.jae464.domain.repository.ProgressTaskRepository
import com.jae464.domain.repository.TaskRepository
import kotlinx.coroutines.flow.flowOf
import java.util.concurrent.Flow
import javax.inject.Inject

class GetProgressTaskUseCase @Inject constructor(
    private val progressTaskRepository: ProgressTaskRepository
) {
    operator fun invoke(progressTaskId: String) = progressTaskRepository.getProgressTask(progressTaskId)

}