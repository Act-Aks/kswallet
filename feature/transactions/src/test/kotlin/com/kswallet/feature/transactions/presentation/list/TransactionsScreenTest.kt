package com.kswallet.feature.transactions.presentation.list

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.kswallet.core.designsystem.theme.KsWalletTheme
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.model.TransactionId
import com.kswallet.core.model.TransactionType
import com.kswallet.core.ui.util.MoneyFormatter
import com.kswallet.feature.transactions.R
import com.kswallet.feature.transactions.domain.model.PeriodOption
import com.kswallet.feature.transactions.domain.model.TransactionFilter
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * The transactions screen, rendered and driven like a user would.
 *
 * These run on the JVM (Robolectric hosts the Android runtime), which is the point: an
 * `androidTest` would need an emulator and would therefore never run in this project's
 * CI or on the machine it is developed on. Every assertion below is about the *screen*,
 * not the ViewModel - the tests above it already cover the state machine - so they
 * answer the questions a ViewModel test cannot: does the ledger group by day with its
 * own subtotal, do the filter chips carry the current selection, and does a tap on a
 * row or a delete icon produce the intent the ViewModel expects.
 */
class TransactionsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val currency = Currency.INR
    private val actions = mutableListOf<TransactionsAction>()

    @Test
    fun `a ledger still loading shows a spinner, not the empty state`() {
        show(TransactionsState(isLoading = true))

        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()
        composeRule.onNodeWithText(str(R.string.transactions_empty_title)).assertDoesNotExist()
    }

    @Test
    fun `an empty ledger says so, and offers the action that fills it`() {
        show(state())

        composeRule.onNodeWithText(str(R.string.transactions_empty_title)).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.transactions_empty_message)).assertIsDisplayed()

        composeRule.onNodeWithContentDescription(str(R.string.transactions_add)).performClick()
        assertEquals(listOf(TransactionsAction.AddTransactionClicked), actions)
    }

    @Test
    fun `the period is summarised as income and expense, formatted`() {
        show(state())

        // The amounts are what the summary is for. ("Income" and "Expense" also
        // label the filter chips below, so the words alone cannot prove the
        // summary card rendered - the numbers can.)
        composeRule.onNodeWithText(money("5000.00")).assertIsDisplayed()
        composeRule.onNodeWithText(money("1800.00")).assertIsDisplayed()
    }

    @Test
    fun `the default filters are shown as selected`() {
        show(state())

        // The first chip of each row, so both are on screen without scrolling.
        composeRule.onNodeWithText(str(R.string.period_this_month)).assert(isSelected())
        composeRule.onNodeWithText(str(R.string.transactions_filter_all)).assert(isSelected())
    }

    @Test
    fun `a period chip sends the period, not a string`() {
        show(state())

        composeRule.onNodeWithText(str(R.string.period_this_month)).performClick()
        assertEquals(listOf(TransactionsAction.PeriodSelected(PeriodOption.THIS_MONTH)), actions)
    }

    @Test
    fun `the all-types chip clears a type filter`() {
        show(state(filter = TransactionFilter(type = TransactionType.EXPENSE)))

        composeRule.onNodeWithText(str(R.string.transactions_filter_all)).performClick()
        assertEquals(listOf(TransactionsAction.TypeFilterSelected(null)), actions)
    }

    @Test
    fun `the ledger is grouped by day, each group with its own signed subtotal`() {
        val dayTotal = Money.of("100.00", currency) - Money.of("245.00", currency)
        show(
            state(
                groups = listOf(
                    DayGroupUi(
                        header = "Today",
                        dayTotal = dayTotal,
                        items = listOf(item(id = 1)),
                    ),
                ),
            ),
        )

        composeRule.onNodeWithText("Today").assertIsDisplayed()
        // The subtotal is signed the way the app signs it - the test asserts the
        // wiring, not the sign convention.
        composeRule.onNodeWithText(MoneyFormatter.formatSigned(dayTotal)).assertIsDisplayed()
        composeRule.onNodeWithText("Coffee").assertIsDisplayed()
        composeRule.onNodeWithText("Groceries · Cafés").assertIsDisplayed()
        composeRule.onNodeWithText(MoneyFormatter.formatSigned(Money.of("45.00", currency))).assertIsDisplayed()
    }

    @Test
    fun `a row tap reports that transaction`() {
        show(state(groups = listOf(dayGroup(item(id = 9, title = "Salary")))))

        composeRule.onNodeWithText("Salary").performClick()
        assertEquals(listOf(TransactionsAction.TransactionClicked(TransactionId(9))), actions)
    }

    @Test
    fun `the delete button on a row reports that transaction`() {
        show(state(groups = listOf(dayGroup(item(id = 9)))))

        composeRule.onNodeWithContentDescription(str(R.string.action_delete)).performClick()
        assertEquals(listOf(TransactionsAction.DeleteClicked(TransactionId(9))), actions)
    }

    @Test
    fun `deleting names the transaction and warns it cannot be undone`() {
        show(state(pendingDelete = PendingTransactionDelete(TransactionId(9), "Coffee")))

        composeRule.onNodeWithText(str(R.string.transaction_delete_title)).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.transaction_delete_message, "Coffee")).assertIsDisplayed()

        composeRule.onNodeWithText(str(R.string.action_delete)).performClick()
        assertEquals(listOf(TransactionsAction.DeleteConfirmed), actions)
    }

    @Test
    fun `walking away from the dialog keeps the transaction`() {
        show(state(pendingDelete = PendingTransactionDelete(TransactionId(9), "Coffee")))

        composeRule.onNodeWithText(CancelLabel).performClick()
        assertEquals(listOf(TransactionsAction.DeleteDismissed), actions)
    }

    // --- plumbing ------------------------------------------------------------------------_

    private fun show(state: TransactionsState) {
        composeRule.setContent {
            KsWalletTheme {
                TransactionsScreen(state = state, onAction = { actions += it })
            }
        }
    }

    private fun state(
        groups: List<DayGroupUi> = emptyList(),
        income: String? = "5000.00",
        expense: String? = "1800.00",
        filter: TransactionFilter = TransactionFilter(),
        pendingDelete: PendingTransactionDelete? = null,
    ) = TransactionsState(
        isLoading = false,
        filter = filter,
        currency = currency,
        dayGroups = groups,
        periodIncome = income?.let { Money.of(it, currency) },
        periodExpense = expense?.let { Money.of(it, currency) },
        pendingDelete = pendingDelete,
    )

    private fun dayGroup(vararg items: TransactionItemUi) = DayGroupUi(
        header = "Today",
        dayTotal = items.fold(Money.zero(currency)) { sum, item -> sum + item.amount },
        items = items.toList(),
    )

    private fun item(
        id: Long = 1,
        title: String = "Coffee",
        subtitle: String = "Groceries · Cafés",
        amount: String = "45.00",
        type: TransactionType = TransactionType.EXPENSE,
    ) = TransactionItemUi(
        id = TransactionId(id),
        title = title,
        subtitle = subtitle,
        amount = Money.of(amount, currency),
        type = type,
        iconKey = "cafe",
        colorArgb = 0xFF112233,
    )

    /** A chip or row carries its selection in its semantics. */
    private fun isSelected() =
        SemanticsMatcher.expectValue(SemanticsProperties.Selected, true)

    /** Strings come from the resources, never from English literals in the test. */
    private fun str(@StringRes id: Int, vararg args: Any): String = context.getString(id, *args)

    /** Money is expected exactly as the app formats it - the test asserts the wiring, not the locale. */
    private fun money(amount: String): String = MoneyFormatter.format(Money.of(amount, currency))

    private companion object {
        /** Owned by the design system's dialog, not by this feature. */
        const val CancelLabel = "Cancel"
    }
}
