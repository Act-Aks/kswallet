package com.kswallet.feature.settings.domain.usecase

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.common.result.outcomeOfSuspend
import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.core.model.Currency

class SetDefaultCurrencyUseCase(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(currency: Currency): Outcome<Unit> = outcomeOfSuspend {
        repository.setDefaultCurrency(currency)
    }
}
