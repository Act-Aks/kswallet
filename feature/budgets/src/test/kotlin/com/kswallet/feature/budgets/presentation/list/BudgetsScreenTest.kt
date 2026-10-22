package com.kswallet.feature.budgets.presentation.list

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kswallet.core.designsystem.theme.KsWalletTheme
import com.kswallet.core.model.BudgetId
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.ui.util.MoneyFormatter
import com.kswallet.feature.budgets.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * The budgets screen, rendered and driven like a user would.
 *
 * These run on the JVM through Robolectric (see `ReportsScreenTest` for why). They are the
 * tests the ViewModel tests cannot be: that the bar a row draws is the row's own fraction, that
 * the over-budget wording is not only a red colour, and - the one that matters most for this
 * screen - that deleting cannot happen without the confirmation dialog, because a budget is
 * deleted by a tap that is easy to make by accident.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class BudgetsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val currency = Currency.INR
    private val intents = mutableListOf<BudgetsAction>()

    /**
     * The label of a Material 3 extended FAB lives behind `clearAndSetSemantics` inside the
     * button, so it is drawn but never reaches the merged semantics tree - the button node
     * carries no text at all. Look it up in the unmerged tree; the click is injected at the
     * label's own position, which lands on the button underneath it.
     */
    private fun fabLabel() =
        composeRule.onNodeWithText(str(R.string.budgets_new), useUnmergedTree = true)

    @Test
    fun `an empty list offers the action that fills it, and no floating button`() {
        show(BudgetsState(isLoading = false, budgets = emptyList()))

        composeRule.onNodeWithText(str(R.string.budgets_empty_title)).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.budgets_empty_action)).performClick()

        // The empty state owns the call to action, so the FAB would be a second one.
        fabLabel().assertDoesNotExist()
        assertEquals(listOf(BudgetsAction.AddBudgetClicked), intents)
    }

    @Test
    fun `a list shows the floating button instead of the empty state`() {
        show(stateWith(row(limit = "1000.00", spent = "400.00")))

        fabLabel().assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.budgets_empty_title)).assertDoesNotExist()

        fabLabel().performClick()
        assertEquals(listOf(BudgetsAction.AddBudgetClicked), intents)
    }

    @Test
    fun `a row says how much of the limit is gone, and tapping it opens the editor`() {
        show(stateWith(row(id = 7, limit = "1000.00", spent = "400.00")))

        composeRule.onNodeWithText("Groceries").assertIsDisplayed()
        composeRule.onNodeWithText(
            str(R.string.budget_spent_of_limit, money("400.00"), money("1000.00")),
        ).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.budget_left, money("600.00"))).assertIsDisplayed()

        composeRule.onNodeWithText("Groceries").performClick()
        assertEquals(listOf(BudgetsAction.BudgetClicked(BudgetId(7))), intents)
    }

    @Test
    fun `a row draws its own progress, not a spinner`() {
        show(stateWith(row(limit = "1000.00", spent = "400.00")))

        // A determinate bar carries a range; the indeterminate one is what the loading state
        // uses, so this also proves the two are not confused.
        composeRule.onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo(0.4f, 0f..1f)),
        ).assertExists()
    }

    @Test
    fun `passing the limit is spelled out, not just coloured`() {
        show(stateWith(row(limit = "500.00", spent = "800.00")))

        composeRule.onNodeWithText(
            str(R.string.budget_spent_of_limit, money("800.00"), money("500.00")),
        ).assertIsDisplayed()
        // The amount over the limit, in words: the row stays readable without colour vision.
        composeRule.onNodeWithText(str(R.string.budget_over_by, money("300.00"))).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.budget_left, money("0.00"))).assertDoesNotExist()
    }

    @Test
    fun `the delete button asks, and only confirming deletes`() {
        show(stateWith(row(id = 3, limit = "1000.00", spent = "400.00")))

        composeRule.onNodeWithContentDescription(str(R.string.action_delete)).performClick()
        assertEquals(listOf(BudgetsAction.DeleteClicked(BudgetId(3))), intents)
    }

    @Test
    fun `the confirmation dialog names the category it is about`() {
        show(stateWith(row(id = 3), pendingDelete = PendingBudgetDelete(BudgetId(3), "Groceries")))

        composeRule.onNodeWithText(str(R.string.budget_delete_title, "Groceries")).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.budget_delete_message)).assertIsDisplayed()

        composeRule.onNodeWithText(str(R.string.action_delete)).performClick()
        assertEquals(listOf(BudgetsAction.DeleteConfirmed), intents)
    }

    @Test
    fun `dismissing the confirmation cancels the delete`() {
        show(stateWith(row(id = 3), pendingDelete = PendingBudgetDelete(BudgetId(3), "Groceries")))

        composeRule.onNodeWithText(CancelLabel).performClick()

        assertEquals(listOf(BudgetsAction.DeleteDismissed), intents)
    }

    @Test
    fun `a list still loading shows a spinner, not the empty state`() {
        show(BudgetsState(isLoading = true, budgets = emptyList()))

        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()
        composeRule.onNodeWithText(str(R.string.budgets_empty_title)).assertDoesNotExist()
    }

    // --- plumbing -------------------------------------------------------------------------

    private fun show(state: BudgetsState) {
        composeRule.setContent {
            KsWalletTheme {
                BudgetsScreen(state = state, onAction = { intents += it })
            }
        }
    }

    private fun stateWith(
        vararg rows: BudgetRowUi,
        pendingDelete: PendingBudgetDelete? = null,
    ) = BudgetsState(
        isLoading = false,
        budgets = rows.toList(),
        pendingDelete = pendingDelete,
    )

    private fun row(
        id: Long = 1,
        limit: String = "1000.00",
        spent: String = "400.00",
        period: BudgetPeriodLabel = BudgetPeriodLabel.MONTHLY,
    ): BudgetRowUi {
        val limitMoney = Money.of(limit, currency)
        val spentMoney = Money.of(spent, currency)
        val fraction = (spentMoney.minorUnits.toFloat() / limitMoney.minorUnits.toFloat()).coerceIn(0f, 1f)
        return BudgetRowUi(
            id = BudgetId(id),
            categoryName = "Groceries",
            iconKey = "groceries",
            colorArgb = 0xFF112233,
            periodLabel = period,
            limit = limitMoney,
            spent = spentMoney,
            fractionUsed = fraction,
            isOverBudget = spentMoney > limitMoney,
        )
    }

    /** Strings come from the resources, never from English literals in the test. */
    private fun str(@StringRes id: Int, vararg args: Any): String = context.getString(id, *args)

    /** Money is expected exactly as the app formats it - the test asserts the wiring, not the locale. */
    private fun money(amount: String): String = MoneyFormatter.format(Money.of(amount, currency))

    private companion object {
        /** Owned by the design system's dialog, not by this feature. */
        const val CancelLabel = "Cancel"
    }
}
