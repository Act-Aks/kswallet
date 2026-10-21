package com.kswallet.feature.accounts.domain.usecase

import com.kswallet.core.model.Account
import com.kswallet.core.model.AccountId
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow

class ObserveAccountUseCase(
    private val repository: AccountRepository,
) {
    operator fun invoke(accountId: AccountId): Flow<Account?> = repository.observeAccount(accountId)
}
