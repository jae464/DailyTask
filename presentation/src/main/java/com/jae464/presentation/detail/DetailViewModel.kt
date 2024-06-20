package com.jae464.presentation.detail

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.extensions.isNotNull
import com.jae464.domain.model.ProgressTask
import com.jae464.domain.usecase.GetProgressTaskUseCase
import com.jae464.domain.usecase.UpdateProgressedTimeUseCase
import com.jae464.domain.usecase.UpdateTodayMemoUseCase
import com.jae464.presentation.home.ProgressTaskUiModel
import com.jae464.presentation.home.ProgressingState
import com.jae464.presentation.home.ProgressingTaskManager
import com.jae464.presentation.home.toProgressTaskUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getProgressTaskUseCase: GetProgressTaskUseCase,
    private val updateProgressedTimeUseCase: UpdateProgressedTimeUseCase,
    private val updateTodayMemoUseCase: UpdateTodayMemoUseCase
) : ViewModel() {

    private val TAG = "DetailViewModel"
    private val progressingTaskManager = ProgressingTaskManager.getInstance()

    private val _uiState: MutableStateFlow<DetailUiState> = MutableStateFlow(DetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var collectProgressingTaskJob: Job? = null
    private lateinit var progressTask: ProgressTask

    init {

        val currentProgressingTask = progressingTaskManager.getCurrentProgressTask()

        viewModelScope.launch {
            getProgressTaskUseCase(savedStateHandle["progressTaskId"] ?: "")
                .collectLatest {
                    if (it == null) {
                        _uiState.emit(DetailUiState.Loading)
                    } else {
                        progressTask = it
                        _uiState.emit(DetailUiState.Success(it.toProgressTaskUiModel()))

                        if (currentProgressingTask?.id == savedStateHandle["progressTaskId"]) {
                            startCollectProgressingTask()
                        }
                    }
                }
        }
    }


    fun startProgressTask(context: Context) {
        // 기존 진행중인 ProgressTask 업데이트
        stopCurrentProgressingTask()
        progressingTaskManager.startProgressTask(progressTask, context)
        startCollectProgressingTask()
    }

    private fun startCollectProgressingTask() {
        if (collectProgressingTaskJob == null) {
            collectProgressingTaskJob = viewModelScope.launch {
                progressingTaskManager.progressingState.collectLatest {
                    if (it is ProgressingState.Progressing) {
                        progressTask = progressTask.copy(progressedTime = it.progressTask.progressedTime)
                        _uiState.emit(
                            DetailUiState.Success(
                                progressTask.toProgressTaskUiModel(true)
                            )
                        )
                    }
                }
            }
        }
    }

    fun stopProgressTask() {
        stopCurrentProgressingTask()
        collectProgressingTaskJob?.cancel()
        collectProgressingTaskJob = null
        viewModelScope.launch {
            _uiState.emit(DetailUiState.Success(progressTask.toProgressTaskUiModel(false)))
        }
    }

    private fun stopCurrentProgressingTask() {
        if (progressingTaskManager.progressingState.value is ProgressingState.Progressing) {
            val progressingTaskId = progressingTaskManager.getCurrentProgressTask()?.id ?: return
            val progressedTime = progressingTaskManager.stopProgressTask()
            viewModelScope.launch {
                updateProgressedTimeUseCase(progressingTaskId, progressedTime)
            }
        }
    }

    fun updateTodayMemo(todayMemo: String) {
        val progressTaskId = progressTask.id
        viewModelScope.launch {
            updateTodayMemoUseCase(progressTaskId, todayMemo)
        }
    }


}

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val progressTaskUiModel: ProgressTaskUiModel) : DetailUiState
}