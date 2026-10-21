package com.kswallet.core.datastore.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kswallet.core.datastore.domain.model.ThemeMode
import com.kswallet.core.datastore.domain.model.UserSettings
import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.core.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineFirstSettingsRepository(
    private val context: Context,
) : SettingsRepository {

    override fun observeSettings(): Flow<UserSettings> = context.settingsDataStore.data.map { preferences ->
        val defaults = UserSettings()
        UserSettings(
            themeMode = preferences[Keys.themeMode]
                ?.let { stored -> ThemeMode.entries.firstOrNull { it.name == stored } }
                ?: defaults.themeMode,
            dynamicColor = preferences[Keys.dynamicColor] ?: defaults.dynamicColor,
            defaultCurrency = preferences[Keys.defaultCurrency]
                ?.let(Currency::fromCode)
                ?: defaults.defaultCurrency,
            onboardingCompleted = preferences[Keys.onboardingCompleted] ?: defaults.onboardingCompleted,
        )
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { it[Keys.themeMode] = mode.name }
    }

    override suspend fun setDynamicColor(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.dynamicColor] = enabled }
    }

    override suspend fun setDefaultCurrency(currency: Currency) {
        context.settingsDataStore.edit { it[Keys.defaultCurrency] = currency.code }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        context.settingsDataStore.edit { it[Keys.onboardingCompleted] = completed }
    }

    private object Keys {
        val themeMode = stringPreferencesKey("theme_mode")
        val dynamicColor = booleanPreferencesKey("dynamic_color")
        val defaultCurrency = stringPreferencesKey("default_currency")
        val onboardingCompleted = booleanPreferencesKey("onboarding_completed")
    }
}
