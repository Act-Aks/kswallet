package com.kswallet.feature.onboarding.presentation.onboarding

import androidx.lifecycle.viewModelScope
import com.kswallet.core.common.result.Outcome
import com.kswallet.core.ui.screen.ScreenViewModel
import com.kswallet.feature.onboarding.domain.usecase.CompleteOnboardingUseCase
import com.kswallet.feature.onboarding.presentation.error.toUiMessage
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class OnboardingViewModel(
    private val completeOnboarding: CompleteOnboardingUseCase,
) : ScreenViewModel<OnboardingState, OnboardingAction, OnboardingEvent>(OnboardingState()) {

    override fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.ContinueClicked -> setState {
                copy(step = (step + 1).coerceAtMost(ONBOARDING_STEP_COUNT - 1))
            }

            OnboardingAction.BackClicked -> setState { copy(step = (step - 1).coerceAtLeast(0)) }

            is OnboardingAction.CurrencySelected -> setState {
                copy(preferences = preferences.copy(defaultCurrency = action.currency))
            }

            is OnboardingAction.ThemeSelected -> setState {
                copy(preferences = preferences.copy(themeMode = action.mode))
            }

            is OnboardingAction.DynamicColorToggled -> setState {
                copy(preferences = preferences.copy(dynamicColor = action.enabled))
            }

            OnboardingAction.FinishClicked -> finish()
        }
    }

    private fun finish() {
        if (state.value.isSaving) return
        val preferences = state.value.preferences
        setState { copy(isSaving = true) }
        viewModelScope.launch {
            when (val result = completeOnboarding(preferences)) {
                is Outcome.Success -> sendEvent(OnboardingEvent.Completed)
                is Outcome.Failure -> {
                    setState { copy(isSaving = false) }
                    sendEvent(OnboardingEvent.ShowMessage(result.error.toUiMessage()))
                }
            }
        }
    }
}
