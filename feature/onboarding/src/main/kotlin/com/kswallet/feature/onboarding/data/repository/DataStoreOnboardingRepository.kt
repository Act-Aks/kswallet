package com.kswallet.feature.onboarding.data.repository

import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.feature.onboarding.domain.model.OnboardingPreferences
import com.kswallet.feature.onboarding.domain.repository.OnboardingRepository
import org.koin.core.annotation.Single

/** Adapts the shared settings store to the onboarding feature's persistence contract. */
@Single(binds = [OnboardingRepository::class])
class DataStoreOnboardingRepository(
    private val settingsRepository: SettingsRepository,
) : OnboardingRepository {
    override suspend fun completeOnboarding(preferences: OnboardingPreferences) {
        // Keep the completion flag last: if a preference write fails, first-run setup can retry.
        settingsRepository.setDefaultCurrency(preferences.defaultCurrency)
        settingsRepository.setThemeMode(preferences.themeMode)
        settingsRepository.setDynamicColor(preferences.dynamicColor)
        settingsRepository.setOnboardingCompleted(true)
    }
}
