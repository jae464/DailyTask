package com.jae464.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.toDayOfWeek
import com.jae464.domain.repository.TaskRepository
import com.jae464.domain.usecase.GetTasksByDayOfWeekUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTasksByDayOfWeekUseCase: GetTasksByDayOfWeekUseCase
) : ViewModel() {
    val tasks =
        getTasksByDayOfWeekUseCase(LocalDate.now().dayOfWeek.toDayOfWeek())
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )


}