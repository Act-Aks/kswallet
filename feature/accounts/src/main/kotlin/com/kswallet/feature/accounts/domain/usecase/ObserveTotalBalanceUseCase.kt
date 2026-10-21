package com.kswallet.feature.accounts.domain.usecase

import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow

class ObserveTotalBalanceUseCase(
    private val repository: AccountRepository,
) {
    operator fun invoke(currency: Currency): Flow<Money> = repository.observeTotalBalance(currency)
}
