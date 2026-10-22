package com.kswallet.feature.categories.domain.validation

sealed interface CategoryFormError {
    data object EmptyName : CategoryFormError
    data object NameTooLong : CategoryFormError
    data object DuplicateName : CategoryFormError
}
