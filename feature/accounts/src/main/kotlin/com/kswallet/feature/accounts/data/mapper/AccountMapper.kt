package com.kswallet.feature.accounts.data.mapper

import com.kswallet.core.database.entity.AccountEntity
import com.kswallet.core.database.model.AccountWithBalance
import com.kswallet.core.model.Account
import com.kswallet.core.model.AccountId
import com.kswallet.core.model.AccountType
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import kotlin.time.Instant

fun AccountWithBalance.toDomain(): Account {
    val currency = Currency.fromCode(account.currencyCode) ?: Currency.Default
    return Account(
        id = AccountId(account.id),
        name = account.name,
        type = AccountType.valueOf(account.type),
        currency = currency,
        balance = Money(balanceMinor, currency),
        initialBalance = Money(account.initialBalanceMinor, currency),
        iconKey = account.iconKey,
        colorArgb = account.colorArgb,
        includedInTotal = account.includedInTotal,
        isArchived = account.isArchived,
        createdAt = Instant.fromEpochMilliseconds(account.createdAt),
    )
}

fun AccountEntity.toDomain(balanceMinor: Long): Account {
    val currency = Currency.fromCode(currencyCode) ?: Currency.Default
    return Account(
        id = AccountId(id),
        name = name,
        type = AccountType.valueOf(type),
        currency = currency,
        balance = Money(balanceMinor, currency),
        initialBalance = Money(initialBalanceMinor, currency),
        iconKey = iconKey,
        colorArgb = colorArgb,
        includedInTotal = includedInTotal,
        isArchived = isArchived,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
    )
}

/**
 * Only the stored fields are mapped: `balance` is computed by the database and must never be
 * written back, so updating an account cannot corrupt the ledger.
 */
fun Account.toEntity(): AccountEntity = AccountEntity(
    id = if (id.isSaved) id.value else 0L,
    name = name,
    type = type.name,
    currencyCode = currency.code,
    initialBalanceMinor = initialBalance.minorUnits,
    iconKey = iconKey,
    colorArgb = colorArgb,
    includedInTotal = includedInTotal,
    isArchived = isArchived,
    createdAt = createdAt.toEpochMilliseconds(),
)
