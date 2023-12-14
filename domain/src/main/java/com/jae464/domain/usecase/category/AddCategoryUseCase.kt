package com.jae464.domain.usecase.category

import com.jae464.domain.model.Category
import com.jae464.domain.repository.CategoryRepository
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) = categoryRepository.addCategory(category)
}