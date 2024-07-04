package com.jae464.presentation.setting

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jae464.domain.model.Category
import com.jae464.domain.usecase.category.AddCategoryUseCase
import com.jae464.domain.usecase.category.ChangeDefaultCategoryUseCase
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryPreferenceUiState(
    val categories: List<Category> = emptyList()
)

sealed interface CategoryPreferenceUiEvent {
    data class AddCategoryEvent(val categoryName: String) : CategoryPreferenceUiEvent
    data class EditCategoryEvent(val categoryId: Long, val categoryName: String) : CategoryPreferenceUiEvent
    data class DeleteCategoryEvent(val categoryId: Long) : CategoryPreferenceUiEvent
    data class ChangeDefaultCategoryEvent(val categoryId: Long) : CategoryPreferenceUiEvent
}

sealed interface CategoryPreferenceUiEffect {
    data object DuplicateCategoryName : CategoryPreferenceUiEffect
}

@HiltViewModel
class CategoryPreferenceViewModel @Inject constructor(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val editCategoryNameUseCase: EditCategoryNameUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val changeDefaultCategoryUseCase: ChangeDefaultCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryPreferenceUiState())
    val uiState: StateFlow<CategoryPreferenceUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CategoryPreferenceUiEffect>()
    val uiEffect: SharedFlow<CategoryPreferenceUiEffect> = _uiEffect.asSharedFlow()

    private val categoryList = mutableListOf<Category>() // 카테고리 이름 중복 체크용

    init {
        getCategories()
    }

    fun handleEvent(event: CategoryPreferenceUiEvent) {
        when (event) {
            is CategoryPreferenceUiEvent.AddCategoryEvent -> {
                addCategory(event.categoryName)
            }
            is CategoryPreferenceUiEvent.ChangeDefaultCategoryEvent -> {
                changeDefaultCategory(event.categoryId)
            }
            is CategoryPreferenceUiEvent.DeleteCategoryEvent -> {
                deleteCategory(event.categoryId)
            }
            is CategoryPreferenceUiEvent.EditCategoryEvent -> {
                editCategoryName(event.categoryId, event.categoryName)
            }
        }
    }

    private fun getCategories() {
        getAllCategoriesUseCase().onEach { categories ->
            _uiState.update { state -> state.copy(categories = categories) }
        }.launchIn(viewModelScope)

//        viewModelScope.launch {
//            getAllCategoriesUseCase().collectLatest { categories ->
//                _uiState.value = CategoryUiState.Success(categories)
//                categoryList.addAll(categories)
//            }
//        }
    }

    private fun addCategory(categoryName: String) {
        val available = isAvailableName(categoryName)
        viewModelScope.launch {
            if (available) {
                addCategoryUseCase(Category(0L, categoryName, false))
            }
            else {
                _uiEffect.emit(CategoryPreferenceUiEffect.DuplicateCategoryName)
            }
        }
    }

    private fun isAvailableName(categoryName: String): Boolean {
        return categoryList.any { it.name == categoryName }.not()
    }


    private fun editCategoryName(categoryId: Long, categoryName: String) {
        viewModelScope.launch {
            editCategoryNameUseCase(categoryId, categoryName)
        }
    }

    private fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            deleteCategoryUseCase(categoryId)
        }
    }

    private fun changeDefaultCategory(categoryId: Long) {
        viewModelScope.launch {
            changeDefaultCategoryUseCase(categoryId)
        }
    }
}
