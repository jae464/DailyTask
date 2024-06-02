package com.jae464.domain.usecase.category

import com.jae464.domain.repository.CategoryRepository
import javax.inject.Inject

class ChangeDefaultCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(categoryId: Long) = categoryRepository.changeDefaultCategory(categoryId)
}