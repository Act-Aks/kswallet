package com.kswallet.feature.settings.presentation.settings

import androidx.lifecycle.viewModelScope
import com.kswallet.core.common.result.Outcome
import com.kswallet.core.ui.screen.ScreenViewModel
import com.kswallet.feature.settings.domain.usecase.ObserveSettingsUseCase
import com.kswallet.feature.settings.domain.usecase.SetDefaultCurrencyUseCase
import com.kswallet.feature.settings.domain.usecase.SetDynamicColorUseCase
import com.kswallet.feature.settings.domain.usecase.SetThemeModeUseCase
import com.kswallet.feature.settings.presentation.error.toUiMessage
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * The settings screen.
 *
 * It never keeps a copy of the settings: every change goes to the repository and the new
 * value comes back through the observed flow, so the screen, the theme and the dashboard can
 * never disagree about what the current setting is.
 */
class SettingsViewModel(
    observeSettings: ObserveSettingsUseCase,
    private val setThemeMode: SetThemeModeUseCase,
    private val setDynamicColor: SetDynamicColorUseCase,
    private val setDefaultCurrency: SetDefaultCurrencyUseCase,
) : ScreenViewModel<SettingsState, SettingsAction, SettingsEvent>(SettingsState()) {

    init {
        observeSettings()
            .onEach { settings -> setState { copy(isLoading = false, settings = settings) } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.ThemeSelected -> apply { setThemeMode(action.mode) }

            is SettingsAction.DynamicColorToggled -> apply { setDynamicColor(action.enabled) }

            is SettingsAction.CurrencySelected -> apply { setDefaultCurrency(action.currency) }

            SettingsAction.BackClicked -> sendEvent(SettingsEvent.NavigateBack)
        }
    }

    /** All three writes are the same shape: run it, and report only if it failed. */
    private fun apply(write: suspend () -> Outcome<Unit>) {
        viewModelScope.launch {
            val result = write()
            if (result is Outcome.Failure) {
                sendEvent(SettingsEvent.ShowMessage(result.error.toUiMessage()))
            }
        }
    }
}
