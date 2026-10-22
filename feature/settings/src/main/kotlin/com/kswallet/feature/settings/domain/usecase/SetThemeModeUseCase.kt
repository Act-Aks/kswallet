package com.kswallet.feature.settings.domain.usecase

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.common.result.outcomeOfSuspend
import com.kswallet.core.datastore.domain.model.ThemeMode
import com.kswallet.core.datastore.domain.repository.SettingsRepository

/** Also used by the app shell: change the theme anywhere, the whole app re-themes. */
class SetThemeModeUseCase(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(mode: ThemeMode): Outcome<Unit> = outcomeOfSuspend {
        repository.setThemeMode(mode)
    }
}
