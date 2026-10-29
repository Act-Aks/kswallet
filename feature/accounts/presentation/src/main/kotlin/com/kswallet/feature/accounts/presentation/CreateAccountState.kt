package com.kswallet.feature.accounts.presentation

data class CreateAccountState(
    val isVisible: Boolean = false,
    val name: String = "",
    val selectedCurrencyCode: String? = null,
    val isSubmitting: Boolean = false,
    val nameError: String? = null,
    val currencyError: String? = null,
    val submissionError: String? = null,
) {

    val canSubmit: Boolean
        get() = !isSubmitting &&
                name.isNotBlank() &&
                selectedCurrencyCode != null
}