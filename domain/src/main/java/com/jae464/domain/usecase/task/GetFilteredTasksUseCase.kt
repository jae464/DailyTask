package com.jae464.domain.usecase.task

import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.SortBy
import com.jae464.domain.model.Task
import com.jae464.domain.model.TaskType
import com.jae464.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetFilteredTasksUseCase @Inject constructor(
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
        useFilterDayOfWeeks: Boolean = false,
        filterDayOfWeeks: Set<DayOfWeek> = emptySet(),
        sortBy: SortBy = SortBy.DESC
    ): Flow<List<Task>> = taskRepository.getFilteredTasks(
        usePeriod,
        startDate,
        endDate,
        useFilterCategory,
        filterCategoryIds,
        useFilterTaskType,
        filterTaskType,
        useFilterDayOfWeeks,
        filterDayOfWeeks,
        sortBy
    )
}