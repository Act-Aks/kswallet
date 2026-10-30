package com.kswallet.feature.accounts.presentation

data class RenameAccountState(
    val accountId: String? = null,
    val currentName: String = "",
    val draftName: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null,
) {

    val isVisible: Boolean
        get() = accountId != null

    val canSubmit: Boolean
        get() = !isSubmitting && draftName.isNotBlank() && draftName != currentName
}