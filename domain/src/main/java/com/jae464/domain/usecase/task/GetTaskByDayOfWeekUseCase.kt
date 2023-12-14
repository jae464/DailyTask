package com.jae464.domain.usecase.task

import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.Task
import com.jae464.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksByDayOfWeekUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(dayOfWeek: DayOfWeek): Flow<List<Task>>
    = taskRepository.getTasksByDayOfWeek(dayOfWeek)
}