package com.kswallet.feature.categories.domain.usecase

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryId
import com.kswallet.feature.categories.domain.repository.CategoryRepository

class GetCategoryUseCase(
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(categoryId: CategoryId): Outcome<Category> = repository.getCategory(categoryId)
}
