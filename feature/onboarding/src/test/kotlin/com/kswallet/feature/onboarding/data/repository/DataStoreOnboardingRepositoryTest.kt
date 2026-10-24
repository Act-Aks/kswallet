package com.kswallet.feature.onboarding.data.repository

import com.kswallet.core.datastore.domain.model.ThemeMode
import com.kswallet.core.datastore.domain.model.UserSettings
import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.core.model.Currency
import com.kswallet.feature.onboarding.domain.model.OnboardingPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DataStoreOnboardingRepositoryTest {
    @Test
    fun `saves preferences before marking onboarding complete`() = runTest {
        val settings = RecordingSettingsRepository()
        val repository = DataStoreOnboardingRepository(settings)

        repository.completeOnboarding(
            OnboardingPreferences(
                defaultCurrency = Currency.USD,
                themeMode = ThemeMode.DARK,
                dynamicColor = true,
            ),
        )

        assertEquals(
            listOf("currency:USD", "theme:DARK", "dynamic:true", "complete:true"),
            settings.writes,
        )
    }

    private class RecordingSettingsRepository : SettingsRepository {
        val writes = mutableListOf<String>()
        override fun observeSettings(): Flow<UserSettings> = flowOf(UserSettings())
        override suspend fun setThemeMode(mode: ThemeMode) {
            writes += "theme:${mode.name}"
        }

        override suspend fun setDynamicColor(enabled: Boolean) {
            writes += "dynamic:$enabled"
        }

        override suspend fun setDefaultCurrency(currency: Currency) {
            writes += "currency:${currency.code}"
        }

        override suspend fun setOnboardingCompleted(completed: Boolean) {
            writes += "complete:$completed"
        }
    }
}
