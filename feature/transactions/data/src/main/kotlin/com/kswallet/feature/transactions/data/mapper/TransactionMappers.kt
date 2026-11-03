package com.kswallet.feature.transactions.data.mapper

import com.kswallet.core.database.entity.TransactionEntity
import com.kswallet.core.identity.AccountId
import com.kswallet.core.identity.TransactionId
import com.kswallet.core.money.CurrencyCode
import com.kswallet.core.money.CurrencyUnit
import com.kswallet.core.money.Money
import com.kswallet.feature.transactions.domain.model.NewTransaction
import com.kswallet.feature.transactions.domain.model.Transaction
import com.kswallet.feature.transactions.domain.model.TransactionNote
import com.kswallet.feature.transactions.domain.model.TransactionTimestamp
import com.kswallet.feature.transactions.domain.model.TransactionType
import java.time.Instant

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = TransactionId.parse(id),
    accountId = AccountId.parse(accountId),
    type = TransactionType.valueOf(type),
    amount = Money(
        minorUnits = amountMinorUnits,
        currency = CurrencyUnit(
            code = CurrencyCode.of(currencyCode),
            minorUnitScale = currencyScale,
        ),
    ),
    timestamp = TransactionTimestamp.fromStored(
        value = Instant.ofEpochMilli(timestampEpochMillis),
    ),
    note = note?.let(TransactionNote::of),
)

fun NewTransaction.toEntity(id: TransactionId): TransactionEntity = TransactionEntity(
    id = id.toString(),
    accountId = accountId.toString(),
    type = type.name,
    amountMinorUnits = amount.minorUnits,
    currencyCode = amount.currency.code.toString(),
    currencyScale = amount.currency.minorUnitScale,
    timestampEpochMillis = timestamp.value.toEpochMilli(),
    note = note?.value,
)