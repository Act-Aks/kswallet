package com.kswallet.feature.accounts.domain.validation

sealed interface AccountFormError {
    data object EmptyName : AccountFormError
    data object NameTooLong : AccountFormError
    data object InvalidInitialBalance : AccountFormError
}
