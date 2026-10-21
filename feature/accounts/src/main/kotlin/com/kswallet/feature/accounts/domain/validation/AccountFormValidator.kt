package com.kswallet.feature.accounts.domain.validation

import com.kswallet.core.model.Money

object AccountFormValidator {

    const val MAX_NAME_LENGTH = 40

    fun validate(input: AccountFormInput): List<AccountFormError> = buildList {
        val name = input.name.trim()
        when {
            name.isEmpty() -> add(AccountFormError.EmptyName)
            name.length > MAX_NAME_LENGTH -> add(AccountFormError.NameTooLong)
        }
        if (input.initialBalanceText.isNotBlank() && parseInitialBalance(input) == null) {
            add(AccountFormError.InvalidInitialBalance)
        }
    }

    /** Blank means zero - a new account does not have to start with money in it. */
    fun parseInitialBalance(input: AccountFormInput): Money? =
        if (input.initialBalanceText.isBlank()) {
            Money.zero(input.currency)
        } else {
            Money.parseOrNull(input.initialBalanceText, input.currency)
        }

    fun normalizeName(name: String): String = name.trim()
}
