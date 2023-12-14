package com.jae464.domain.usecase.progresstask

import com.jae464.domain.repository.ProgressTaskRepository
import com.jae464.domain.repository.TaskRepository
import java.time.LocalDate
import javax.inject.Inject

class GetProgressTaskByDates @Inject constructor(
    private val progressTaskRepository: ProgressTaskRepository
){
    operator fun invoke(startDate: LocalDate, endDate: LocalDate) = progressTaskRepository.getProgressTasksByDate(startDate, endDate)
}