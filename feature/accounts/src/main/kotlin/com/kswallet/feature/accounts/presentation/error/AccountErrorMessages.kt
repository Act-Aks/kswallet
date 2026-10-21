package com.kswallet.feature.accounts.presentation.error

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.accounts.R
import com.kswallet.feature.accounts.presentation.editor.AccountEditorError

internal fun DomainError.toUiMessage(): UiMessage = when (this) {
    DomainError.NotFound -> UiMessage.Resource(R.string.error_not_found)
    is DomainError.Conflict -> UiMessage.Plain(reason)
    is DomainError.Data -> UiMessage.Resource(R.string.error_storage)
    is DomainError.Unknown -> UiMessage.Resource(R.string.error_unknown)
}

internal fun AccountEditorError.toUiMessage(): UiMessage = when (this) {
    AccountEditorError.EmptyName -> UiMessage.Resource(R.string.account_error_empty_name)
    AccountEditorError.NameTooLong -> UiMessage.Resource(R.string.account_error_name_too_long)
    AccountEditorError.InvalidInitialBalance -> UiMessage.Resource(R.string.account_error_invalid_balance)
    AccountEditorError.Unknown -> UiMessage.Resource(R.string.error_unknown)
}
