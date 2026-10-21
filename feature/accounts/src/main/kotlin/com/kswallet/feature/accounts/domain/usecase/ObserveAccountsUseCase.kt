package com.kswallet.feature.accounts.domain.usecase

import com.kswallet.core.model.Account
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow

class ObserveAccountsUseCase(
    private val repository: AccountRepository,
) {
    operator fun invoke(): Flow<List<Account>> = repository.observeAccounts()
}
