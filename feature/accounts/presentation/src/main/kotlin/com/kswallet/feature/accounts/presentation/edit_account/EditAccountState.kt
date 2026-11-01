package com.kswallet.feature.accounts.presentation.edit_account

import com.kswallet.core.screen.ScreenState

data class EditAccountState(
    val isLoading: Boolean = true,
    val accountId: String? = null,
    val name: String = "",
    val currencyCode: String = "",
    val isArchived: Boolean = false,
    val isSaving: Boolean = false,
    val nameError: String? = null,
    val actionError: String? = null,
) : ScreenState {

    val canSave: Boolean
        get() = !isLoading &&
                !isSaving &&
                name.isNotBlank()
}