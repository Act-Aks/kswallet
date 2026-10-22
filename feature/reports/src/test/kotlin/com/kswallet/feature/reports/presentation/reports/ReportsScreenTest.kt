package com.kswallet.feature.reports.presentation.reports

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import com.kswallet.core.designsystem.theme.KsWalletTheme
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.ui.util.DateFormatter
import com.kswallet.core.ui.util.MoneyFormatter
import com.kswallet.feature.reports.R
import com.kswallet.feature.reports.domain.model.ReportMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

/**
 * The reports screen, rendered and driven like a user would.
 *
 * These run on the JVM (Robolectric hosts the Android runtime), which is the point: an
 * `androidTest` would need an emulator and would therefore never run in this project's CI or on
 * the machine it is developed on. Every assertion below is about the *screen*, not the
 * ViewModel - the tests above it already cover the state machine - so they answer the questions
 * a ViewModel test cannot: is the empty state really shown instead of a summary of zeros, does
 * the arrow that is disabled look disabled, does a tap produce the intent the ViewModel expects,
 * and is the number on screen the number the domain computed.
 */
class ReportsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val currency = Currency.INR
    private val actions = mutableListOf<ReportsAction>()

    private val october = ReportMonth(2026, 10)

    @Test
    fun `a month still loading shows a spinner, not an empty month`() {
        show(state(isLoading = true))

        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()
        composeRule.onNodeWithText(str(R.string.reports_empty_title)).assertDoesNotExist()
    }

    @Test
    fun `a month without movement is the empty state, and the summary is not printed with zeros`() {
        show(
            state(
                isLoading = false,
                income = "0.00",
                expense = "0.00",
            ),
        )

        composeRule.onNodeWithText(str(R.string.reports_empty_title)).assertIsDisplayed()
        composeRule.onNodeWithText(
            str(R.string.reports_empty_message, DateFormatter.monthAndYear(LocalDate.of(2026, 10, 1))),
        ).assertIsDisplayed()
        // The whole point of the empty state: no summary card full of ₹0.00.
        composeRule.onNodeWithText(str(R.string.reports_spent)).assertDoesNotExist()
    }

    @Test
    fun `the summary shows the month, what was spent, and how it compares`() {
        show(
            state(
                isLoading = false,
                income = "2000.00",
                expense = "1200.00",
                daysElapsed = 12,
                change = 0.2f,
            ),
        )

        composeRule.onNodeWithText(DateFormatter.monthAndYear(LocalDate.of(2026, 10, 1))).assertIsDisplayed()
        composeRule.onNodeWithText(money("1200.00")).assertIsDisplayed()
        composeRule.onNodeWithText(money("2000.00")).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.reports_change_up, "20%")).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.reports_average_per_day, money("100.00"))).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.reports_in_progress, 12, 31)).assertIsDisplayed()
    }

    @Test
    fun `spending less than last month says so, in words rather than only in colour`() {
        show(state(isLoading = false, expense = "800.00", change = -0.25f))

        composeRule.onNodeWithText(str(R.string.reports_change_down, "25%")).assertIsDisplayed()
    }

    @Test
    fun `a month with nothing to compare against explains why instead of showing a percentage`() {
        show(state(isLoading = false, expense = "800.00", change = null))

        composeRule.onNodeWithText(str(R.string.reports_no_comparison)).assertIsDisplayed()
    }

    @Test
    fun `the arrows send the navigation actions, and the future one is disabled on the current month`() {
        show(state(isLoading = false, expense = "800.00", canGoNext = false))

        composeRule.onNodeWithContentDescription(str(R.string.reports_next_month)).assertIsNotEnabled()

        composeRule.onNodeWithContentDescription(str(R.string.reports_previous_month)).performClick()
        assertEquals(listOf(ReportsAction.PreviousMonthClicked), actions)
    }

    @Test
    fun `walking forward is possible once a past month is shown`() {
        show(state(isLoading = false, expense = "800.00", canGoNext = true))

        composeRule.onNodeWithContentDescription(str(R.string.reports_next_month)).performClick()

        assertEquals(listOf(ReportsAction.NextMonthClicked), actions)
    }

    @Test
    fun `the categories explain where the money went, shares and entry counts included`() {
        show(
            state(
                isLoading = false,
                expense = "1200.00",
                categories = listOf(
                    CategoryShareUi(7, "Groceries", "groceries", 0xFF112233, Money.of("900.00", currency), 0.75f, 4),
                    CategoryShareUi(null, null, null, null, Money.of("300.00", currency), 0.25f, 1),
                ),
            ),
        )

        // The category list sits below the chart, so the test scrolls to it the way a user
        // would - `assertIsDisplayed` is about what is on screen, not about what exists.
        scrollTo("Groceries")

        composeRule.onNodeWithText(str(R.string.reports_where_it_went)).assertIsDisplayed()
        composeRule.onNodeWithText("Groceries").assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.reports_share_of_spending, 75)).assertIsDisplayed()
        composeRule.onNodeWithText(money("900.00")).assertIsDisplayed()
        composeRule.onNodeWithText(plural(R.plurals.reports_entry_count, 4, 4)).assertIsDisplayed()

        // Unfiled spending is a row of its own, labelled by the screen.
        scrollTo(str(R.string.reports_uncategorised))

        composeRule.onNodeWithText(str(R.string.reports_uncategorised)).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.reports_share_of_spending, 25)).assertIsDisplayed()
    }

    @Test
    fun `a broken read offers a retry that reaches the ViewModel`() {
        show(state(isLoading = false, hasFailed = true))

        composeRule.onNodeWithText(str(R.string.reports_error_load)).assertIsDisplayed()
        composeRule.onNodeWithText(RetryLabel).performClick()
        assertTrue(actions.any { it == ReportsAction.RetryClicked })
    }

    // --- plumbing -------------------------------------------------------------------------

    private fun show(state: ReportsState) {
        composeRule.setContent {
            KsWalletTheme {
                ReportsScreen(state = state, onAction = { actions += it })
            }
        }
    }

    private fun state(
        isLoading: Boolean,
        income: String = "2000.00",
        expense: String = "1200.00",
        daysElapsed: Int = 12,
        change: Float? = 0.2f,
        canGoNext: Boolean = false,
        categories: List<CategoryShareUi> = emptyList(),
        hasFailed: Boolean = false,
    ) = ReportsState(
        month = october,
        isLoading = isLoading,
        canGoNext = canGoNext,
        currency = currency,
        income = Money.of(income, currency),
        expense = Money.of(expense, currency),
        net = Money.of(income, currency) - Money.of(expense, currency),
        averagePerDay = Money.of(expense, currency).let { Money(it.minorUnits / maxOf(daysElapsed, 1), currency) },
        daysElapsed = daysElapsed,
        isCurrentMonth = daysElapsed < october.daysInMonth,
        changeVsPreviousMonth = change,
        categories = categories,
        dailyExpenses = List(october.daysInMonth) { Money.zero(currency) },
        hasFailed = hasFailed,
    )

    /** Scrolls the screen's lazy list until [text] is on screen. */
    private fun scrollTo(text: String) {
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(text))
    }

    /** Strings come from the resources, never from English literals in the test. */
    private fun str(@StringRes id: Int, vararg args: Any): String = context.getString(id, *args)

    private fun plural(@PluralsRes id: Int, count: Int, vararg args: Any): String =
        context.resources.getQuantityString(id, count, *args)

    /** Money is expected exactly as the app formats it - the test asserts the wiring, not the locale. */
    private fun money(amount: String): String = MoneyFormatter.format(Money.of(amount, currency))

    private companion object {
        /** `KsErrorState` in the design system owns this label; it is not a feature string. */
        const val RetryLabel = "Retry"
    }
}
