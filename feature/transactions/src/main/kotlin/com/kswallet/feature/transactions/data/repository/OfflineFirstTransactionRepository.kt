package com.kswallet.feature.transactions.data.repository

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.common.result.outcomeOfSuspend
import com.kswallet.core.common.time.DateRange
import com.kswallet.core.database.dao.AccountDao
import com.kswallet.core.database.dao.TransactionDao
import com.kswallet.core.model.AccountId
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Transaction
import com.kswallet.core.model.TransactionId
import com.kswallet.core.model.TransactionType
import com.kswallet.feature.transactions.data.mapper.toDomain
import com.kswallet.feature.transactions.data.mapper.toEntity
import com.kswallet.feature.transactions.domain.model.CashFlow
import com.kswallet.feature.transactions.domain.model.TransactionDetails
import com.kswallet.feature.transactions.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single(binds = [TransactionRepository::class])
class OfflineFirstTransactionRepository(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
) : TransactionRepository {

    override fun observeTransactions(
        range: DateRange,
        type: TransactionType?,
        accountId: AccountId?,
        categoryId: CategoryId?,
        limit: Int,
    ): Flow<List<TransactionDetails>> = transactionDao.observeTransactions(
        from = range.start.toEpochMilliseconds(),
        to = range.endExclusive.toEpochMilliseconds(),
        type = type?.name,
        accountId = accountId?.value,
        categoryId = categoryId?.value,
        limit = limit,
    ).map { rows -> rows.map { it.toDomain() } }

    override fun observeRecentTransactions(limit: Int): Flow<List<TransactionDetails>> =
        transactionDao.observeRecentTransactions(limit).map { rows -> rows.map { it.toDomain() } }

    override fun observeCashFlow(range: DateRange, currency: Currency): Flow<CashFlow> =
        transactionDao.observeCashFlow(
            from = range.start.toEpochMilliseconds(),
            to = range.endExclusive.toEpochMilliseconds(),
            currencyCode = currency.code,
        ).map { row -> row.toDomain(currency) }

    override suspend fun getTransaction(transactionId: TransactionId): Outcome<Transaction> = outcomeOfSuspend {
        val entity = transactionDao.findById(transactionId.value)
            ?: throw NoSuchElementException("Transaction ${transactionId.value} not found")
        val currency = accountDao.findCurrencyCode(entity.accountId)
            ?.let(Currency::fromCode)
            ?: Currency.Default
        entity.toDomain(currency)
    }

    override suspend fun upsertTransaction(transaction: Transaction): Outcome<TransactionId> = outcomeOfSuspend {
        if (transaction.id.isSaved) {
            transactionDao.update(transaction.toEntity())
            transaction.id
        } else {
            TransactionId(transactionDao.insert(transaction.toEntity()))
        }
    }

    override suspend fun deleteTransaction(transactionId: TransactionId): Outcome<Unit> = outcomeOfSuspend {
        val entity = transactionDao.findById(transactionId.value)
            ?: throw NoSuchElementException("Transaction ${transactionId.value} not found")
        transactionDao.delete(entity)
    }
}
