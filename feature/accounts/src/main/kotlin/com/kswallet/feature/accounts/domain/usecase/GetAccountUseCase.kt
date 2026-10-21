package com.kswallet.feature.accounts.domain.usecase

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.model.Account
import com.kswallet.core.model.AccountId
import com.kswallet.feature.accounts.domain.repository.AccountRepository

class GetAccountUseCase(
    private val repository: AccountRepository,
) {
    suspend operator fun invoke(accountId: AccountId): Outcome<Account> = repository.getAccount(accountId)
}
