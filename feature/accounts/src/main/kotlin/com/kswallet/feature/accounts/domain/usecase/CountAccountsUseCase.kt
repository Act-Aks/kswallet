package com.kswallet.feature.accounts.domain.usecase

import com.kswallet.core.common.result.Outcome
import com.kswallet.feature.accounts.domain.repository.AccountRepository

class CountAccountsUseCase(
    private val repository: AccountRepository,
) {
    suspend operator fun invoke(): Outcome<Int> = repository.countAccounts()
}
