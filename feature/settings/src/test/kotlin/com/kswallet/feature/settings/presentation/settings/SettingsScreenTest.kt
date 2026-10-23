package com.kswallet.feature.settings.presentation.settings

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.kswallet.core.datastore.domain.model.ThemeMode
import com.kswallet.core.datastore.domain.model.UserSettings
import com.kswallet.core.designsystem.theme.KsWalletTheme
import com.kswallet.core.model.Currency
import com.kswallet.feature.settings.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * The settings screen, rendered and driven like a user would.
 *
 * These run on the JVM (Robolectric hosts the Android runtime), which is the
 * point: an `androidTest` would need an emulator and would therefore never run
 * in this project's CI or on the machine it is developed on. This screen is
 * the one place the user's choices are made, so the tests pin the contract the
 * DataStore relies on: every control sends the intent that carries the full
 * new value (a theme, a boolean, a currency), and the value that is already
 * saved is the one shown as selected.
 */
class SettingsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val intents = mutableListOf<SettingsAction>()

    @Test
    fun `a screen still loading shows a spinner, not empty sections`() {
        show(SettingsState(isLoading = true))

        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()
        composeRule.onNodeWithText(str(R.string.settings_section_appearance)).assertDoesNotExist()
    }

    @Test
    fun `every control the DataStore stores is on the screen`() {
        show(state())

        composeRule.onNodeWithText(str(R.string.settings_section_appearance)).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.settings_theme)).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.settings_theme_system)).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.settings_theme_light)).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.settings_theme_dark)).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.settings_dynamic_color)).assertIsDisplayed()

        scrollTo(str(R.string.settings_default_currency))
        composeRule.onNodeWithText(str(R.string.settings_default_currency)).assertIsDisplayed()

        // The rows are rendered from the currency list the ViewModel hands down.
        scrollTo("INR · ₹")
        composeRule.onNodeWithText("INR · ₹").assertIsDisplayed()

        scrollTo("USD · $")
        composeRule.onNodeWithText("USD · $").assertIsDisplayed()

        // The about note is long text at the very bottom; the test
        // display is small enough to clip it, so the meaningful check
        // is that the screen renders it at all.
        scrollTo(str(R.string.settings_about))
        composeRule.onNodeWithText(str(R.string.settings_about)).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.settings_about_note)).assertExists()
    }

    @Test
    fun `the saved theme is the chip shown as selected`() {
        show(state(settings = UserSettings(themeMode = ThemeMode.DARK)))

        composeRule.onNodeWithText(str(R.string.settings_theme_dark)).assert(isSelected())
        composeRule.onNodeWithText(str(R.string.settings_theme_system)).assertIsDisplayed()
    }

    @Test
    fun `a theme chip sends the whole mode, not a toggle`() {
        show(state())

        composeRule.onNodeWithText(str(R.string.settings_theme_dark)).performClick()
        assertEquals(listOf(SettingsAction.ThemeSelected(ThemeMode.DARK)), intents)
    }

    @Test
    fun `the wallpaper-colour switch sends the new boolean`() {
        show(state())

        // The switch is the only Switch-role control on the screen; the currency
        // rows are radio buttons, so the role is what tells them apart.
        composeRule.onNode(switchRole()).performClick()
        assertEquals(listOf(SettingsAction.DynamicColorToggled(true)), intents)
    }

    @Test
    fun `a currency row sends that currency, and the base one starts selected`() {
        show(state())

        composeRule.onNodeWithText("INR · ₹").assert(isSelected())

        scrollTo("USD · $")
        composeRule.onNodeWithText("USD · $").performClick()
        assertEquals(listOf(SettingsAction.CurrencySelected(Currency.USD)), intents)
    }

    @Test
    fun `a saved base currency is the row shown as selected`() {
        show(state(settings = UserSettings(defaultCurrency = Currency.EUR)))

        scrollTo("EUR · €")
        composeRule.onNodeWithText("EUR · €").assert(isSelected())
    }

    // --- plumbing ------------------------------------------------------------------------_

    private fun show(state: SettingsState) {
        composeRule.setContent {
            KsWalletTheme {
                SettingsScreen(state = state, onAction = { intents += it })
            }
        }
    }

    private fun state(
        settings: UserSettings = UserSettings(),
        currencies: List<Currency> = Currency.entries,
    ) = SettingsState(
        isLoading = false,
        settings = settings,
        currencies = currencies,
    )

    /**
     * The screen is one scrollable column, not a lazy list: every control is
     * composed, so a later card is reached by scrolling the node's own
     * ancestor container until the node is on screen.
     */
    private fun scrollTo(text: String) {
        composeRule.onNodeWithText(text).performScrollTo()
    }

    private fun switchRole() =
        SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch)

    /** A chip or a currency row carries its selection in its semantics. */
    private fun isSelected() =
        SemanticsMatcher.expectValue(SemanticsProperties.Selected, true)

    /** Strings come from the resources, never from English literals in the test. */
    private fun str(@StringRes id: Int, vararg args: Any): String = context.getString(id, *args)
}
