package com.kswallet.feature.accounts.domain.model

enum class AccountNameError {
    Empty,
    ControlCharacter,
    TooLong,
}