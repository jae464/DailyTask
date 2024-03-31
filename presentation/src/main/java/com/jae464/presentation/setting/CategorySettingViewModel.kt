package com.jae464.presentation.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.Category
import com.jae464.domain.usecase.category.AddCategoryUseCase
import com.jae464.domain.usecase.category.DeleteCategoryUseCase
import com.jae464.domain.usecase.category.EditCategoryNameUseCase
import com.jae464.domain.usecase.category.GetAllCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

sealed interface CategorySettingEvent {
    object DuplicateCategoryName : CategorySettingEvent
}
@HiltViewModel
class CategorySettingViewModel @Inject constructor(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val editCategoryNameUseCase: EditCategoryNameUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> get() = _uiState.asStateFlow()

    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> get() = _counter.asStateFlow()

    private val _event = MutableSharedFlow<CategorySettingEvent>()
    val event: SharedFlow<CategorySettingEvent> get() = _event.asSharedFlow()

    private val categoryList = mutableListOf<Category>() // 카테고리 이름 중복 체크용

    init {
        getCategories()
    }

    private fun getCategories() {
        viewModelScope.launch {
            getAllCategoriesUseCase().collectLatest { categories ->
                _uiState.value = CategoryUiState.Success(categories)
                categoryList.addAll(categories)
            }
        }
    }

    fun addCategory(categoryName: String) {
        val available = isAvailableName(categoryName)
        viewModelScope.launch {
            if (available) {
                addCategoryUseCase(Category(0L, categoryName, false))
            }
            else {
                _event.emit(CategorySettingEvent.DuplicateCategoryName)
            }
        }
    }

    private fun isAvailableName(categoryName: String): Boolean {
        return categoryList.any { it.name == categoryName }.not()
    }

    fun updateCounter(counter: Int) {
        _counter.value = counter
    }

    fun editCategoryName(categoryId: Long, categoryName: String) {
        viewModelScope.launch {
            editCategoryNameUseCase(categoryId, categoryName)
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            deleteCategoryUseCase(categoryId)
        }
    }

    fun changeDefaultCategory(categoryId: Long) {

    }
}
