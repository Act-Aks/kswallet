package com.kswallet.feature.accounts.domain.usecase

import com.kswallet.core.model.Currency
import com.kswallet.feature.accounts.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow

/** Accounts that hold a different currency than the base one - shown as a footnote. */
class ObserveForeignCurrencyAccountsUseCase(
    private val repository: AccountRepository,
) {
    operator fun invoke(baseCurrency: Currency): Flow<Int> = repository.observeForeignCurrencyAccountCount(baseCurrency)
}
