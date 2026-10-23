package com.kswallet.feature.settings.presentation.settings

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.datastore.domain.model.ThemeMode
import com.kswallet.core.datastore.domain.model.UserSettings
import com.kswallet.core.datastore.domain.repository.SettingsRepository
import com.kswallet.core.model.Currency
import com.kswallet.core.testing.MainDispatcherExtension
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.settings.R
import com.kswallet.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.kswallet.feature.settings.domain.usecase.SetDefaultCurrencyUseCase
import com.kswallet.feature.settings.domain.usecase.SetDynamicColorUseCase
import com.kswallet.feature.settings.domain.usecase.SetThemeModeUseCase
import com.kswallet.feature.settings.presentation.error.toUiMessage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.IOException

/**
 * Settings is small but not trivial: the screen must follow the *stored* value rather than
 * the tap, and a failed write must be reported instead of silently swallowed.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension(testDispatcher)

    private val repository = mockk<SettingsRepository>(relaxed = true)
    private val stored = MutableStateFlow(UserSettings())

    private fun viewModel() = SettingsViewModel(
        observeSettings = ObserveSettingsUseCase(repository),
        setThemeMode = SetThemeModeUseCase(repository),
        setDynamicColor = SetDynamicColorUseCase(repository),
        setDefaultCurrency = SetDefaultCurrencyUseCase(repository),
    )

    private fun stubStoredSettings() {
        every { repository.observeSettings() } returns stored
    }

    @Test
    fun `loads the stored settings and every currency`() = runTest(testDispatcher) {
        every { repository.observeSettings() } returns MutableStateFlow(
            UserSettings(themeMode = ThemeMode.DARK, defaultCurrency = Currency.EUR),
        )

        val viewModel = viewModel()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(ThemeMode.DARK, viewModel.state.value.settings.themeMode)
        assertEquals(Currency.EUR, viewModel.state.value.settings.defaultCurrency)
        assertEquals(Currency.entries.size, viewModel.state.value.currencies.size)
    }

    @Test
    fun `the screen follows the stored value, not the tapped one`() = runTest(testDispatcher) {
        stubStoredSettings()
        val viewModel = viewModel()

        viewModel.onAction(SettingsAction.ThemeSelected(ThemeMode.LIGHT))

        coVerify { repository.setThemeMode(ThemeMode.LIGHT) }
        // The write goes to DataStore; the state only changes once DataStore emits. That is
        // what keeps this screen honest (and what a settings screen usually gets wrong).
        assertEquals(ThemeMode.SYSTEM, viewModel.state.value.settings.themeMode)

        stored.value = UserSettings(themeMode = ThemeMode.LIGHT)
        assertEquals(ThemeMode.LIGHT, viewModel.state.value.settings.themeMode)
    }

    @Test
    fun `choosing a base currency writes it`() = runTest(testDispatcher) {
        stubStoredSettings()
        val viewModel = viewModel()

        viewModel.onAction(SettingsAction.CurrencySelected(Currency.USD))

        coVerify { repository.setDefaultCurrency(Currency.USD) }
    }

    @Test
    fun `toggling dynamic colour writes the switch value`() = runTest(testDispatcher) {
        stubStoredSettings()
        val viewModel = viewModel()

        viewModel.onAction(SettingsAction.DynamicColorToggled(true))

        coVerify { repository.setDynamicColor(true) }
    }

    @Test
    fun `a failed write is reported as a storage problem`() = runTest(testDispatcher) {
        stubStoredSettings()
        coEvery { repository.setDefaultCurrency(any()) } throws IOException("disk full")
        val viewModel = viewModel()

        viewModel.onAction(SettingsAction.CurrencySelected(Currency.USD))

        assertEquals(
            SettingsEvent.ShowMessage(UiMessage.Resource(R.string.error_storage)),
            viewModel.events.first(),
        )
    }

    @Test
    fun `the back intent becomes a navigation effect`() = runTest(testDispatcher) {
        stubStoredSettings()
        val viewModel = viewModel()

        viewModel.onAction(SettingsAction.BackClicked)

        assertEquals(SettingsEvent.NavigateBack, viewModel.events.first())
    }

    @Test
    fun `storage failures read as storage problems, everything else generically`() {
        assertEquals(
            UiMessage.Resource(R.string.error_storage),
            DomainError.Data(IOException("disk full")).toUiMessage(),
        )
        assertEquals(
            UiMessage.Resource(R.string.error_unknown),
            DomainError.Unknown(IllegalStateException("boom")).toUiMessage(),
        )
    }
}
