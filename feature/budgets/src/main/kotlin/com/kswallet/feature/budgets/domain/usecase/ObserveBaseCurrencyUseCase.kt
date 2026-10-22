package com.kswallet.feature.budgets.domain.usecase

import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.core.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The currency a new budget is counted in: the user's base currency.
 *
 * Reading it here (rather than in the ViewModel) keeps the screen free of the settings
 * repository, and means a change of base currency is picked up live.
 */
class ObserveBaseCurrencyUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<Currency> = settingsRepository.observeSettings().map { it.defaultCurrency }
}
