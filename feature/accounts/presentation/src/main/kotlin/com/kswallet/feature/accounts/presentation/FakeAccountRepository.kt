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

    var nextCreateFailure: Exception? = null

    var observationStarts = 0
        private set

    var activeObservers = 0
        private set

    var emitAfterDelay = false

    private var emissionReleased = false

    private val releaseSignal = kotlinx.coroutines.CompletableDeferred<Unit>()

    override fun observeAccounts(): Flow<List<Account>> = flow {
        observationStarts++
        activeObservers++

        try {
            val failure = nextObservationFailure
            nextObservationFailure = null

            if (failure != null) {
                throw failure
            }

            if (emitAfterDelay) {
                releaseSignal.await()
            }

            emitAll(accounts)
        } finally {
            activeObservers--
        }
    }

    fun releaseEmission() {
        emissionReleased = true
        releaseSignal.complete(Unit)
    }

    override suspend fun findById(id: AccountId): Account? =
        accounts.value.firstOrNull { account ->
            account.id == id
        }

    override suspend fun create(account: NewAccount): Account {
        val failure = nextCreateFailure
        nextCreateFailure = null

        if (failure != null) {
            throw failure
        }

        val created = Account(
            id = AccountId.new(),
            name = account.name,
            currency = account.currency,
            status = AccountStatus.Active,
        )

        accounts.value += created

        return created
    }

    override suspend fun rename(
        id: AccountId,
        name: AccountName,
    ): Account? {
        error("Not used by create-account tests.")
    }

    override suspend fun setStatus(
        id: AccountId,
        status: AccountStatus,
    ): Account? {
        error("Not used by create-account tests.")
    }
}