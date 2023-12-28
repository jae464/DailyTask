package com.jae464.domain.usecase.progresstask

import com.jae464.domain.model.DayOfWeek
import com.jae464.domain.model.TaskType
import com.jae464.domain.repository.ProgressTaskRepository
import java.time.LocalDate
import javax.inject.Inject

class GetFilteredProgressTaskUseCase @Inject constructor(
    private val progressTaskRepository: ProgressTaskRepository
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
        filterDayOfWeeks: Set<DayOfWeek> = emptySet()
    ) = progressTaskRepository.getFilteredProgressTasks(
        usePeriod, startDate, endDate , useFilterCategory, filterCategoryIds, useFilterTaskType, filterTaskType, useFilterDayOfWeeks, filterDayOfWeeks
    )
}