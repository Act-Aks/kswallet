package com.kswallet.feature.accounts.presentation.create_account

import com.kswallet.core.screen.ScreenState

data class CreateAccountState(
    val name: String = "",
    val selectedCurrencyCode: String? = null,
    val isSubmitting: Boolean = false,
    val nameError: String? = null,
    val currencyError: String? = null,
    val submissionError: String? = null,
) : ScreenState {

    val canSubmit: Boolean
        get() = !isSubmitting &&
                name.isNotBlank() &&
                selectedCurrencyCode != null
}