package com.jae464.presentation.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.usecase.progresstask.GetProgressTaskByDates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val getProgressTaskByDates: GetProgressTaskByDates
) : ViewModel() {

    val progressTasks = MutableStateFlow<List<ProgressTask>>(emptyList())

    fun getProgressTasks(startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            getProgressTaskByDates(startDate, endDate).collectLatest {
                progressTasks.value = it
            }
        }
    }

}