package com.jae464.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.Category
import com.jae464.domain.usecase.category.GetAllCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategorySettingUiState(
    val categoryUiState: CategoryUiState = CategoryUiState.Loading,
    val testCounter: Int = 0
)

sealed interface CategoryUiState {
    object Loading : CategoryUiState
    data class Success(val categories: List<Category>) : CategoryUiState
    object Failure : CategoryUiState
}


@HiltViewModel
class CategorySettingViewModel @Inject constructor(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategorySettingUiState())
    val uiState: StateFlow<CategorySettingUiState>
        get() = _uiState.asStateFlow()

    init {
        getCategories()
    }

    private fun getCategories() {
        viewModelScope.launch {
            getAllCategoriesUseCase().collectLatest { categories ->
                _uiState.update {
                    it.copy(
                        categoryUiState = CategoryUiState.Success(categories)
                    )
                }
            }
        }
    }

    fun updateCounter(counter: Int) {
        _uiState.update {
            it.copy(testCounter = counter)
        }
    }
}
