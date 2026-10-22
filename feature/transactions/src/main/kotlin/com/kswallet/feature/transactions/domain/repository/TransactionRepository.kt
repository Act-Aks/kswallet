package com.kswallet.feature.transactions.domain.repository

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.common.time.DateRange
import com.kswallet.core.model.AccountId
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Transaction
import com.kswallet.core.model.TransactionId
import com.kswallet.core.model.TransactionType
import com.kswallet.feature.transactions.domain.model.CashFlow
import com.kswallet.feature.transactions.domain.model.TransactionDetails
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeTransactions(
        range: DateRange,
        type: TransactionType? = null,
        accountId: AccountId? = null,
        categoryId: CategoryId? = null,
        limit: Int = DEFAULT_LIMIT,
    ): Flow<List<TransactionDetails>>

    fun observeRecentTransactions(limit: Int): Flow<List<TransactionDetails>>

    /** Income and expense totals of a period for the accounts of one currency. */
    fun observeCashFlow(range: DateRange, currency: Currency): Flow<CashFlow>

    suspend fun getTransaction(transactionId: TransactionId): Outcome<Transaction>

    suspend fun upsertTransaction(transaction: Transaction): Outcome<TransactionId>

    suspend fun deleteTransaction(transactionId: TransactionId): Outcome<Unit>

    companion object {
        const val DEFAULT_LIMIT = 500
    }
}
