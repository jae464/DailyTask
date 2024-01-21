package com.jae464.domain.usecase.progresstask

import com.jae464.domain.repository.ProgressTaskRepository
import javax.inject.Inject

class DeleteAllProgressTaskByTaskIdUseCase @Inject constructor(
    private val progressTaskRepository: ProgressTaskRepository
){
    suspend operator fun invoke(taskId: String) = progressTaskRepository.deleteAllProgressTaskByTaskId(taskId)
}