package com.kswallet.feature.settings.domain.usecase

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.common.result.outcomeOfSuspend
import com.kswallet.core.datastore.domain.repository.SettingsRepository

class SetDynamicColorUseCase(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean): Outcome<Unit> = outcomeOfSuspend {
        repository.setDynamicColor(enabled)
    }
}
