package com.kswallet.feature.budgets.presentation.error

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.mvi.UiMessage
import com.kswallet.feature.budgets.R
import com.kswallet.feature.budgets.domain.validation.BudgetFormError
import com.kswallet.feature.budgets.presentation.editor.BudgetEditorError

/** One place, per feature, that turns a failure into words. */
fun DomainError.toUiMessage(): UiMessage = when (this) {
    DomainError.NotFound -> UiMessage.Resource(R.string.error_not_found)
    is DomainError.Conflict -> UiMessage.Resource(R.string.error_storage)
    is DomainError.Data -> UiMessage.Resource(R.string.error_storage)
    is DomainError.Unknown -> UiMessage.Resource(R.string.error_unknown)
}

fun BudgetFormError.toUiMessage(): UiMessage = when (this) {
    BudgetFormError.CategoryMissing -> UiMessage.Resource(R.string.budget_error_category_missing)
    BudgetFormError.InvalidLimit -> UiMessage.Resource(R.string.budget_error_invalid_limit)
    BudgetFormError.LimitNotPositive -> UiMessage.Resource(R.string.budget_error_limit_not_positive)
}

fun BudgetEditorError.toUiMessage(): UiMessage = when (this) {
    BudgetEditorError.CategoryMissing -> UiMessage.Resource(R.string.budget_error_category_missing)
    BudgetEditorError.InvalidLimit -> UiMessage.Resource(R.string.budget_error_invalid_limit)
    BudgetEditorError.LimitNotPositive -> UiMessage.Resource(R.string.budget_error_limit_not_positive)
    BudgetEditorError.Unknown -> UiMessage.Resource(R.string.error_unknown)
}
