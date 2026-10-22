package com.kswallet.feature.categories.domain.usecase

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.model.CategoryId
import com.kswallet.feature.categories.domain.validation.CategoryFormError

/** Result of a save: either it happened, or the form is invalid. */
sealed interface SaveCategoryResult {
    data class Saved(val id: CategoryId) : SaveCategoryResult
    data class Invalid(val errors: List<CategoryFormError>) : SaveCategoryResult
    data class Failed(val error: DomainError) : SaveCategoryResult
}
