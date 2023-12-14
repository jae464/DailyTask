package com.jae464.domain.usecase.category

import com.jae464.domain.model.Category
import com.jae464.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(categoryId: Long): Flow<Category> =
        categoryRepository.getCategory(categoryId)
}