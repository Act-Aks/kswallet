package com.kswallet.feature.onboarding.presentation.onboarding

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kswallet.core.designsystem.theme.KsWalletTheme
import com.kswallet.core.model.Currency
import com.kswallet.feature.onboarding.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val intents = mutableListOf<OnboardingAction>()
    private fun text(res: Int): String = context.getString(res)

    @Test
    fun `welcome screen presents the setup and advances`() {
        show(OnboardingState())

        composeRule.onNodeWithText(text(R.string.onboarding_welcome_title)).assertExists()
        composeRule.onNodeWithText(text(R.string.onboarding_benefit_private)).assertExists()
        composeRule.onNodeWithText(text(R.string.onboarding_continue)).performClick()
        assertEquals(OnboardingAction.ContinueClicked, intents.last())
    }

    @Test
    fun `currency option sends its typed currency`() {
        show(OnboardingState(step = 1))

        composeRule.onNodeWithText("USD · $").performClick()
        assertEquals(OnboardingAction.CurrencySelected(Currency.USD), intents.last())
    }

    @Test
    fun `appearance page offers a finish action`() {
        show(OnboardingState(step = 2))

        composeRule.onNodeWithText(text(R.string.onboarding_dynamic_color)).assertExists()
        composeRule.onNodeWithText(text(R.string.onboarding_finish)).performClick()
        assertEquals(OnboardingAction.FinishClicked, intents.last())
    }

    private fun show(state: OnboardingState) {
        composeRule.setContent {
            KsWalletTheme {
                OnboardingScreen(state = state, onAction = intents::add)
            }
        }
    }
}
