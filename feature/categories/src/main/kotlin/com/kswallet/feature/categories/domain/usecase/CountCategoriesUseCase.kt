package com.kswallet.feature.categories.domain.usecase

import com.kswallet.core.common.result.Outcome
import com.kswallet.feature.categories.domain.repository.CategoryRepository

class CountCategoriesUseCase(
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(): Outcome<Int> = repository.countCategories()
}
