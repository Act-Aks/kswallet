package com.kswallet.feature.reports.domain.usecase

import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.core.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** A report is only meaningful in one currency: the one the user thinks in. */
class ObserveBaseCurrencyUseCase(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<Currency> = settingsRepository.observeSettings().map { it.defaultCurrency }
}
