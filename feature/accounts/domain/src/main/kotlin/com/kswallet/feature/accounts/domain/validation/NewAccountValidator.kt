package com.kswallet.feature.accounts.domain.validation

import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.CurrencyRegistry
import com.kswallet.feature.accounts.domain.model.AccountName
import com.kswallet.feature.accounts.domain.model.AccountNameResult
import com.kswallet.feature.accounts.domain.model.NewAccount

class NewAccountValidator(
    private val currencyRegistry: CurrencyRegistry,
) {

    fun validate(
        nameInput: String,
        currencyCode: CurrencyCode?,
    ): NewAccountValidation {
        val name = when (val result = AccountName.parse(nameInput)) {
            is AccountNameResult.Valid -> result.name

            is AccountNameResult.Invalid -> {
                return NewAccountValidation.Invalid(
                    NewAccountError.InvalidName(result.reason),
                )
            }
        }

        if (currencyCode == null) {
            return NewAccountValidation.Invalid(
                NewAccountError.CurrencyRequired,
            )
        }

        val currency = currencyRegistry.find(currencyCode)
            ?: return NewAccountValidation.Invalid(
                NewAccountError.UnsupportedCurrency(currencyCode),
            )

        return NewAccountValidation.Valid(
            NewAccount(
                name = name,
                currency = currency,
            ),
        )
    }
}