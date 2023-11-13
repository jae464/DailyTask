package com.jae464.domain.usecase

import com.jae464.domain.repository.TaskRepository
import java.time.LocalDate
import javax.inject.Inject

class GetProgressTaskByDates @Inject constructor(
    private val taskRepository: TaskRepository
){
    operator fun invoke(startDate: LocalDate, endDate: LocalDate) = taskRepository.getProgressTasksByDate(startDate, endDate)
}