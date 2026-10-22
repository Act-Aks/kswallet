package com.kswallet.feature.categories.presentation.error

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.categories.R
import com.kswallet.feature.categories.domain.validation.CategoryFormError
import com.kswallet.feature.categories.presentation.editor.CategoryEditorError

/**
 * One place, per feature, that turns a failure into words.
 *
 * ViewModels stay platform-free while the text stays translatable, and adding a language
 * never means touching business logic.
 */
fun DomainError.toUiMessage(): UiMessage = when (this) {
    DomainError.NotFound -> UiMessage.Resource(R.string.error_not_found)
    is DomainError.Conflict -> UiMessage.Resource(R.string.error_conflict)
    is DomainError.Data -> UiMessage.Resource(R.string.error_storage)
    is DomainError.Unknown -> UiMessage.Resource(R.string.error_unknown)
}

fun CategoryFormError.toUiMessage(): UiMessage = when (this) {
    CategoryFormError.EmptyName -> UiMessage.Resource(R.string.category_error_empty_name)
    CategoryFormError.NameTooLong -> UiMessage.Resource(R.string.category_error_name_too_long)
    CategoryFormError.DuplicateName -> UiMessage.Resource(R.string.category_error_duplicate)
}

fun CategoryEditorError.toUiMessage(): UiMessage = when (this) {
    CategoryEditorError.EmptyName -> UiMessage.Resource(R.string.category_error_empty_name)
    CategoryEditorError.NameTooLong -> UiMessage.Resource(R.string.category_error_name_too_long)
    CategoryEditorError.DuplicateName -> UiMessage.Resource(R.string.category_error_duplicate)
    CategoryEditorError.Unknown -> UiMessage.Resource(R.string.error_unknown)
}
