package com.jae464.presentation.home

import com.jae464.domain.model.ProgressTask

sealed interface ProgressTaskState {
    object Loading: ProgressTaskState
    data class Success(val progressTasks: List<ProgressTask>): ProgressTaskState
}