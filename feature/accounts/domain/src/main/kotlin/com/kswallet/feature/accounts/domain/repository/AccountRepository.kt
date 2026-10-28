package com.kswallet.feature.accounts.domain.repository

import com.kswallet.feature.accounts.domain.model.Account
import com.kswallet.feature.accounts.domain.model.AccountId
import com.kswallet.feature.accounts.domain.model.AccountName
import com.kswallet.feature.accounts.domain.model.AccountStatus
import com.kswallet.feature.accounts.domain.model.NewAccount
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    /**
     * Observes active and archived accounts.
     *
     * Implementations provide deterministic ordering.
     */
    fun observeAccounts(): Flow<List<Account>>

    suspend fun findById(id: AccountId): Account?

    /**
     * Creates an active account with a new identity.
     *
     * The implementation must validate the currency against its registry.
     */
    suspend fun create(account: NewAccount): Account

    /**
     * Returns the updated account, or null if the ID does not exist.
     */
    suspend fun rename(
        id: AccountId,
        name: AccountName,
    ): Account?

    /**
     * Returns the updated account, or null if the ID does not exist.
     */
    suspend fun setStatus(
        id: AccountId,
        status: AccountStatus,
    ): Account?
}