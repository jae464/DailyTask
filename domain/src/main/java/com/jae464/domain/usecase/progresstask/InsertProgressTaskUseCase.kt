package com.jae464.domain.usecase.progresstask

import com.jae464.domain.model.ProgressTask
import com.jae464.domain.repository.ProgressTaskRepository
import javax.inject.Inject

class InsertProgressTaskUseCase @Inject constructor(
    private val progressTaskRepository: ProgressTaskRepository
) {
    suspend operator fun invoke(progressTask: ProgressTask) = progressTaskRepository.insertProgressTask(progressTask)
}