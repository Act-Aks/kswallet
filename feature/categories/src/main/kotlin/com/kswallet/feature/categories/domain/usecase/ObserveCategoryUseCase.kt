package com.kswallet.feature.categories.domain.usecase

import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryId
import com.kswallet.feature.categories.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveCategoryUseCase(
    private val repository: CategoryRepository,
) {
    operator fun invoke(categoryId: CategoryId): Flow<Category?> = repository.observeCategory(categoryId)
}
