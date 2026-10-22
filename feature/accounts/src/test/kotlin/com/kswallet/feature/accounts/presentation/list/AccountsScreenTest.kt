package com.kswallet.feature.accounts.presentation.list

import android.content.Context
import androidx.annotation.PluralsRes
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
import com.kswallet.core.model.AccountId
import com.kswallet.core.model.AccountType
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.ui.util.MoneyFormatter
import com.kswallet.feature.accounts.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * The accounts screen, rendered and driven like a user would.
 *
 * These run on the JVM (Robolectric hosts the Android runtime), which is the point: an
 * `androidTest` would need an emulator and would therefore never run in this project's CI or on
 * the machine it is developed on. Every assertion below is about the *screen*, not the
 * ViewModel - the tests above it already cover the state machine - so they answer the questions
 * a ViewModel test cannot: is the empty state really shown instead of a list of nothing, does
 * the account left out of the total say so in words, and does a tap produce the intent the
 * ViewModel expects.
 */
@RunWith(AndroidJUnit4::class)
// SDK 35, not 36: Robolectric 4.17's Android 16 runtime fails to boot inside a host JVM
// (`ApplicationSharedMemory.create` reaches for `FileDescriptor` internals that the JRE no
// longer exposes, with or without `--add-opens`). The tests are about this app's screens, not
// about the platform version it is compiled against, so the newest *working* runtime wins.
@Config(sdk = [35])
class AccountsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val currency = Currency.INR
    private val intents = mutableListOf<AccountsAction>()

    @Test
    fun `a list still loading shows a spinner, not the empty state`() {
        show(AccountsState(isLoading = true))

        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()
        composeRule.onNodeWithText(str(R.string.accounts_empty_title)).assertDoesNotExist()
    }

    @Test
    fun `no accounts is an empty state, and no total of nothing`() {
        show(state())

        composeRule.onNodeWithText(str(R.string.accounts_empty_title)).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.accounts_empty_message)).assertIsDisplayed()
        // The whole point of the empty state: no total card over ₹0.00.
        composeRule.onNodeWithText(str(R.string.accounts_total_balance)).assertDoesNotExist()
    }

    @Test
    fun `the total is shown up front, with the currencies it leaves out named`() {
        show(
            state(
                accounts = listOf(account()),
                totalBalance = Money.of("2400.00", currency),
                hiddenForeignCurrencyAccounts = 2,
            ),
        )

        composeRule.onNodeWithText(str(R.string.accounts_total_balance)).assertIsDisplayed()
        composeRule.onNodeWithText(money("2400.00")).assertIsDisplayed()
        // A count in words, so the total is not silently missing money.
        composeRule.onNodeWithText(
            plural(R.plurals.accounts_foreign_currency_note, 2, 2),
        ).assertIsDisplayed()
    }

    @Test
    fun `a row shows its name, type and balance, and tapping it reports the account`() {
        show(state(accounts = listOf(account(id = 7, name = "Savings", balance = "850.25"))))

        composeRule.onNodeWithText("Savings").assertIsDisplayed()
        composeRule.onNodeWithText(
            str(R.string.account_subtitle_included, str(R.string.account_type_bank)),
        ).assertIsDisplayed()
        composeRule.onNodeWithText(money("850.25")).assertIsDisplayed()

        composeRule.onNodeWithText("Savings").performClick()
        assertEquals(listOf(AccountsAction.AccountClicked(AccountId(7))), intents)
    }

    @Test
    fun `an account left out of the total says so next to its type`() {
        show(state(accounts = listOf(account(type = AccountType.CASH, includedInTotal = false))))

        composeRule.onNodeWithText(
            str(R.string.account_subtitle_excluded, str(R.string.account_type_cash)),
        ).assertIsDisplayed()
    }

    @Test
    fun `the floating button offers a new account`() {
        show(state(accounts = listOf(account())))

        composeRule.onNodeWithContentDescription(str(R.string.accounts_add)).performClick()
        assertEquals(listOf(AccountsAction.AddAccountClicked), intents)
    }

    @Test
    fun `the delete button on a row reports that row`() {
        show(state(accounts = listOf(account(id = 4))))

        composeRule.onNodeWithContentDescription(str(R.string.action_delete)).performClick()
        assertEquals(listOf(AccountsAction.DeleteClicked(AccountId(4))), intents)
    }

    @Test
    fun `deleting an account with history warns about it, and only confirming deletes`() {
        show(
            state(
                accounts = listOf(account(id = 4)),
                pendingDelete = PendingAccountDelete(AccountId(4), "Savings", 3),
            ),
        )

        composeRule.onNodeWithText(str(R.string.account_delete_title, "Savings")).assertIsDisplayed()
        composeRule.onNodeWithText(
            plural(R.plurals.account_delete_message_with_transactions, 3, 3),
        ).assertIsDisplayed()

        composeRule.onNodeWithText(str(R.string.action_delete)).performClick()
        assertEquals(listOf(AccountsAction.DeleteConfirmed), intents)
    }

    @Test
    fun `walking away from the dialog keeps the account`() {
        show(state(pendingDelete = PendingAccountDelete(AccountId(4), "Savings", 0)))

        // No transactions: the plain message, not the plural one.
        composeRule.onNodeWithText(str(R.string.account_delete_message)).assertIsDisplayed()

        composeRule.onNodeWithText(CancelLabel).performClick()
        assertEquals(listOf(AccountsAction.DeleteDismissed), intents)
    }

    // --- plumbing ------------------------------------------------------------------------_

    private fun show(state: AccountsState) {
        composeRule.setContent {
            KsWalletTheme {
                AccountsScreen(state = state, onAction = { intents += it })
            }
        }
    }

    private fun state(
        accounts: List<AccountCardUi> = emptyList(),
        totalBalance: Money? = if (accounts.isEmpty()) null else Money.of("2400.00", currency),
        hiddenForeignCurrencyAccounts: Int = 0,
        pendingDelete: PendingAccountDelete? = null,
    ) = AccountsState(
        isLoading = false,
        accounts = accounts,
        totalBalance = totalBalance,
        hiddenForeignCurrencyAccounts = hiddenForeignCurrencyAccounts,
        pendingDelete = pendingDelete,
    )

    private fun account(
        id: Long = 1,
        name: String = "Savings",
        type: AccountType = AccountType.BANK,
        balance: String = "850.25",
        includedInTotal: Boolean = true,
    ) = AccountCardUi(
        id = AccountId(id),
        name = name,
        type = type,
        currency = currency,
        balance = Money.of(balance, currency),
        colorArgb = 0xFF112233,
        includedInTotal = includedInTotal,
    )

    /** Strings come from the resources, never from English literals in the test. */
    private fun str(@StringRes id: Int, vararg args: Any): String = context.getString(id, *args)

    private fun plural(@PluralsRes id: Int, count: Int, vararg args: Any): String =
        context.resources.getQuantityString(id, count, *args)

    /** Money is expected exactly as the app formats it - the test asserts the wiring, not the locale. */
    private fun money(amount: String): String = MoneyFormatter.format(Money.of(amount, currency))

    private companion object {
        /** Owned by the design system's dialog, not by this feature. */
        const val CancelLabel = "Cancel"
    }
}
