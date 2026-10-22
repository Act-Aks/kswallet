package com.kswallet.feature.categories.domain.usecase

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.common.result.fold
import com.kswallet.core.common.time.TimeProvider
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryId
import com.kswallet.feature.categories.domain.repository.CategoryRepository
import com.kswallet.feature.categories.domain.validation.CategoryFormInput
import com.kswallet.feature.categories.domain.validation.CategoryFormValidator

class SaveCategoryUseCase(
    private val repository: CategoryRepository,
    private val timeProvider: TimeProvider,
) {
    /** @param existing the category being edited, or null for a new one. */
    suspend operator fun invoke(input: CategoryFormInput, existing: Category?): SaveCategoryResult {
        val name = CategoryFormValidator.normalizeName(input.name)
        val duplicateCount = repository
            .countCategoriesWithName(input.type, name, existing?.id ?: CategoryId.UNSAVED)
            .let { outcome -> if (outcome is Outcome.Success) outcome.value else 0 }

        val errors = CategoryFormValidator.validate(input) { duplicateCount > 0 }
        if (errors.isNotEmpty()) return SaveCategoryResult.Invalid(errors)

        val now = timeProvider.now()
        val category = Category(
            id = existing?.id ?: CategoryId.UNSAVED,
            name = name,
            type = input.type,
            iconKey = input.iconKey,
            colorArgb = input.colorArgb,
            createdAt = existing?.createdAt ?: now,
        )

        return repository.upsertCategory(category).fold(
            onSuccess = { SaveCategoryResult.Saved(it) },
            onFailure = { SaveCategoryResult.Failed(it) },
        )
    }
}
