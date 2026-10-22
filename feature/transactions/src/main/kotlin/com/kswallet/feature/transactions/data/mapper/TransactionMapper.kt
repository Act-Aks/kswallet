package com.kswallet.feature.transactions.data.mapper

import com.kswallet.core.database.entity.TransactionEntity
import com.kswallet.core.database.model.CashFlowRow
import com.kswallet.core.database.model.TransactionWithDetailsRow
import com.kswallet.core.model.AccountId
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.model.Transaction
import com.kswallet.core.model.TransactionId
import com.kswallet.core.model.TransactionType
import com.kswallet.feature.transactions.domain.model.CashFlow
import com.kswallet.feature.transactions.domain.model.TransactionDetails
import kotlin.time.Instant

fun TransactionWithDetailsRow.toDomain(): TransactionDetails {
    val currency = Currency.fromCode(accountCurrencyCode) ?: Currency.Default
    return TransactionDetails(
        transaction = Transaction(
            id = TransactionId(transaction.id),
            type = TransactionType.valueOf(transaction.type),
            amount = Money(transaction.amountMinor, currency),
            accountId = AccountId(transaction.accountId),
            transferAccountId = transaction.transferAccountId?.let(::AccountId),
            categoryId = transaction.categoryId?.let(::CategoryId),
            dateTime = Instant.fromEpochMilliseconds(transaction.dateTime),
            note = transaction.note,
            createdAt = Instant.fromEpochMilliseconds(transaction.createdAt),
        ),
        accountName = accountName,
        transferAccountName = transferAccountName,
        categoryName = categoryName,
        categoryIconKey = categoryIconKey,
        categoryColorArgb = categoryColorArgb,
    )
}

fun TransactionEntity.toDomain(currency: Currency): Transaction = Transaction(
    id = TransactionId(id),
    type = TransactionType.valueOf(type),
    amount = Money(amountMinor, currency),
    accountId = AccountId(accountId),
    transferAccountId = transferAccountId?.let(::AccountId),
    categoryId = categoryId?.let(::CategoryId),
    dateTime = Instant.fromEpochMilliseconds(dateTime),
    note = note,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = if (id.isSaved) id.value else 0L,
    type = type.name,
    amountMinor = amount.minorUnits,
    accountId = accountId.value,
    transferAccountId = transferAccountId?.value,
    categoryId = categoryId?.value,
    dateTime = dateTime.toEpochMilliseconds(),
    note = note,
    createdAt = createdAt.toEpochMilliseconds(),
)

fun CashFlowRow.toDomain(currency: Currency): CashFlow = CashFlow(
    income = Money(incomeMinor, currency),
    expense = Money(expenseMinor, currency),
)
