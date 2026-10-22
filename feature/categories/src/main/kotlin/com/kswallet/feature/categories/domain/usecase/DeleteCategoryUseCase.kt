package com.kswallet.feature.categories.domain.usecase

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.model.CategoryId
import com.kswallet.feature.categories.domain.repository.CategoryRepository

class DeleteCategoryUseCase(
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(categoryId: CategoryId): Outcome<Unit> = repository.deleteCategory(categoryId)
}
