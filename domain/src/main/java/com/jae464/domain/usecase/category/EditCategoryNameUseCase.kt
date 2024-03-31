package com.jae464.domain.usecase.category

import com.jae464.domain.repository.CategoryRepository
import javax.inject.Inject

class EditCategoryNameUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
){
    suspend operator fun invoke(categoryId: Long, categoryName: String) = categoryRepository.editCategory(categoryId, categoryName)
}