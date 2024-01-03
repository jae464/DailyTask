package com.jae464.domain.usecase.progresstask

import com.jae464.domain.repository.ProgressTaskRepository
import java.time.LocalDate
import javax.inject.Inject

class GetProgressTaskByTaskIdUseCase @Inject constructor(
    private val progressTaskRepository: ProgressTaskRepository
) {
    operator fun invoke(
        usePeriod: Boolean = false,
        taskId: String,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now()
    ) = progressTaskRepository.getProgressTasksByTaskId(
        usePeriod, taskId, startDate, endDate
    )
}