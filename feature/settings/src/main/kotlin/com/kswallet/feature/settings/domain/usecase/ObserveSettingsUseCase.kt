package com.kswallet.feature.settings.domain.usecase

import com.kswallet.core.datastore.domain.model.UserSettings
import com.kswallet.core.datastore.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

/**
 * The settings screen's use cases.
 *
 * Reading is a stream (the theme can change while the screen is open); writing returns an
 * [com.kswallet.core.common.result.Outcome], because a DataStore write can fail on a full disk and the UI must be able to say
 * so instead of pretending the change stuck.
 */
class ObserveSettingsUseCase(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): Flow<UserSettings> = repository.observeSettings()
}
