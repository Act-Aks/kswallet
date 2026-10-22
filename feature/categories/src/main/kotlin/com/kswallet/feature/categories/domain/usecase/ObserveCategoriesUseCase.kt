package com.kswallet.feature.categories.domain.usecase

import com.kswallet.core.model.CategoryType
import com.kswallet.feature.categories.domain.model.CategorySummary
import com.kswallet.feature.categories.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/** Every use case is a tiny class with an `invoke` operator: readable, injectable, testable. */

class ObserveCategoriesUseCase(
    private val repository: CategoryRepository,
) {
    /** @param type null means "both income and expense". */
    operator fun invoke(type: CategoryType? = null): Flow<List<CategorySummary>> {
        val categories = if (type == null) repository.observeCategories() else repository.observeCategories(type)
        return categories.combine(repository.observeUsage()) { list, usage ->
            list.map { category -> CategorySummary(category, usage[category.id] ?: 0) }
        }
    }
}

