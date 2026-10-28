package com.kswallet.feature.accounts.presentation

import com.kswallet.feature.accounts.domain.model.Account
import com.kswallet.feature.accounts.domain.model.AccountId
import com.kswallet.feature.accounts.domain.model.AccountName
import com.kswallet.feature.accounts.domain.model.AccountStatus
import com.kswallet.feature.accounts.domain.model.NewAccount
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

internal class FakeAccountRepository : AccountRepository {

    val accounts = MutableStateFlow<List<Account>>(emptyList())

    var nextObservationFailure: Exception? = null

    var observationStarts: Int = 0
        private set

    var activeObservers: Int = 0
        private set

    override fun observeAccounts(): Flow<List<Account>> = flow {
        observationStarts += 1
        activeObservers += 1

        try {
            val failure = nextObservationFailure
            nextObservationFailure = null

            if (failure != null) {
                throw failure
            }

            emitAll(accounts)
        } finally {
            activeObservers -= 1
        }
    }

    override suspend fun findById(id: AccountId): Account? =
        error("Not used by accounts-list tests.")

    override suspend fun create(account: NewAccount): Account =
        error("Not used by accounts-list tests.")

    override suspend fun rename(
        id: AccountId,
        name: AccountName,
    ): Account? = error("Not used by accounts-list tests.")

    override suspend fun setStatus(
        id: AccountId,
        status: AccountStatus,
    ): Account? = error("Not used by accounts-list tests.")
}