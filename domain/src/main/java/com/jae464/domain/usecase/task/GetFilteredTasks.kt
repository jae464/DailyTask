package com.jae464.domain.usecase.task

import com.jae464.domain.model.TaskType
import com.jae464.domain.repository.TaskRepository
import java.time.LocalDate
import javax.inject.Inject

class GetFilteredTasks @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(
        usePeriod: Boolean = false,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now(),
        useFilterCategory: Boolean = false,
        filterCategoryIds: Set<Long> = emptySet(),
        useFilterTaskType: Boolean = false,
        filterTaskType: TaskType = TaskType.Regular,
    ) = taskRepository.getFilteredTasks(usePeriod, startDate, endDate, useFilterCategory, filterCategoryIds, useFilterTaskType, filterTaskType)
}