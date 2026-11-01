package com.kswallet.feature.transactions.domain.repository

import com.kswallet.core.identity.AccountId
import com.kswallet.core.identity.TransactionId
import com.kswallet.feature.transactions.domain.model.NewTransaction
import com.kswallet.feature.transactions.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    /**
     * Observes transactions for one account.
     *
     * Implementations must return transactions in deterministic order,
     * normally the newest first.
     */
    fun observeByAccount(accountId: AccountId): Flow<List<Transaction>>

    suspend fun findById(id: TransactionId): Transaction?

    suspend fun create(transaction: NewTransaction): Transaction
}