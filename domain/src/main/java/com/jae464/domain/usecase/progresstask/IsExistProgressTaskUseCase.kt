package com.jae464.domain.usecase.progresstask

import com.jae464.domain.repository.ProgressTaskRepository
import java.time.LocalDate
import javax.inject.Inject

class IsExistProgressTaskUseCase @Inject constructor(
    private val progressTaskRepository: ProgressTaskRepository
) {
    suspend operator fun invoke(taskId: String, createdAt: LocalDate) = progressTaskRepository.isExistProgressTaskByDate(taskId, createdAt)
}