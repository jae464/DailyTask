package com.jae464.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.usecase.GetProgressTaskUseCase
import com.jae464.presentation.home.toProgressTaskUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getProgressTaskUseCase: GetProgressTaskUseCase

) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val progressTask = savedStateHandle.getStateFlow(
        key = "progressTaskId",
        initialValue = ""
    )
        .flatMapLatest { progressTaskId ->
            getProgressTaskUseCase(progressTaskId = progressTaskId)
        }
        .map {
            it.toProgressTaskUiModel()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

}