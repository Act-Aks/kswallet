package com.kswallet.feature.accounts.data.mapper

import com.kswallet.core.database.account.AccountEntity
import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.CurrencyUnit
import com.kswallet.feature.accounts.data.error.MalformedAccountRecordException
import com.kswallet.feature.accounts.domain.model.Account
import com.kswallet.feature.accounts.domain.model.AccountId
import com.kswallet.feature.accounts.domain.model.AccountName
import com.kswallet.feature.accounts.domain.model.AccountNameResult
import com.kswallet.feature.accounts.domain.model.AccountStatus

internal fun Account.toEntity(): AccountEntity = AccountEntity(
    id = id.toString(),
    name = name.value,
    currencyCode = currency.code.value,
    currencyScale = currency.minorUnitScale,
    archived = isArchived,
)

internal fun AccountEntity.toDomain(): Account {
    try {
        val accountId = AccountId.parse(id)

        require(accountId.toString() == id) {
            "Stored account ID must use canonical lowercase UUID text."
        }

        val accountName = when (val result = AccountName.parse(name)) {
            is AccountNameResult.Valid -> result.name

            is AccountNameResult.Invalid -> {
                throw IllegalArgumentException(
                    "Stored account name is invalid: ${result.reason}.",
                )
            }
        }

        require(accountName.value == name) {
            "Stored account name must already be normalized."
        }

        val code = CurrencyCode.of(currencyCode)

        require(code.value == currencyCode) {
            "Stored currency code must already be normalized."
        }

        val currency = CurrencyUnit(
            code = code,
            minorUnitScale = currencyScale,
        )

        return Account(
            id = accountId,
            name = accountName,
            currency = currency,
            status = if (archived) {
                AccountStatus.Archived
            } else {
                AccountStatus.Active
            },
        )
    } catch (cause: IllegalArgumentException) {
        throw MalformedAccountRecordException(
            storedAccountId = id,
            cause = cause,
        )
    }
}