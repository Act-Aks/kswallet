package com.kswallet.feature.onboarding.presentation.onboarding

import com.kswallet.core.datastore.domain.model.ThemeMode
import com.kswallet.core.model.Currency
import com.kswallet.core.testing.MainDispatcherExtension
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.onboarding.R
import com.kswallet.feature.onboarding.domain.model.OnboardingPreferences
import com.kswallet.feature.onboarding.domain.repository.OnboardingRepository
import com.kswallet.feature.onboarding.domain.usecase.CompleteOnboardingUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension(testDispatcher)

    private val repository = RecordingOnboardingRepository()
    private fun viewModel() = OnboardingViewModel(CompleteOnboardingUseCase(repository))

    @Test
    fun `starts on welcome and exposes every supported currency`() {
        val viewModel = viewModel()
        assertEquals(0, viewModel.state.value.step)
        assertEquals(Currency.entries, viewModel.state.value.currencies)
    }

    @Test
    fun `steps clamp at both ends`() {
        val viewModel = viewModel()
        viewModel.onAction(OnboardingAction.BackClicked)
        assertEquals(0, viewModel.state.value.step)
        repeat(ONBOARDING_STEP_COUNT + 1) { viewModel.onAction(OnboardingAction.ContinueClicked) }
        assertEquals(ONBOARDING_STEP_COUNT - 1, viewModel.state.value.step)
    }

    @Test
    fun `selected preferences are persisted before completion`() = runTest(testDispatcher) {
        val viewModel = viewModel()
        viewModel.onAction(OnboardingAction.CurrencySelected(Currency.USD))
        viewModel.onAction(OnboardingAction.ThemeSelected(ThemeMode.DARK))
        viewModel.onAction(OnboardingAction.DynamicColorToggled(true))
        viewModel.onAction(OnboardingAction.FinishClicked)

        assertEquals(
            OnboardingPreferences(Currency.USD, ThemeMode.DARK, true),
            repository.completedWith,
        )
        assertEquals(OnboardingEvent.Completed, viewModel.events.first())
    }

    @Test
    fun `failed save keeps setup open and reports a retryable message`() = runTest(testDispatcher) {
        repository.failure = IOException("disk full")
        val viewModel = viewModel()
        viewModel.onAction(OnboardingAction.FinishClicked)

        assertFalse(viewModel.state.value.isSaving)
        assertEquals(
            OnboardingEvent.ShowMessage(UiMessage.Resource(R.string.onboarding_error_storage)),
            viewModel.events.first(),
        )
        assertTrue(repository.completedWith == null)
    }

    private class RecordingOnboardingRepository : OnboardingRepository {
        var completedWith: OnboardingPreferences? = null
        var failure: Throwable? = null
        override suspend fun completeOnboarding(preferences: OnboardingPreferences) {
            failure?.let { throw it }
            completedWith = preferences
        }
    }
}
