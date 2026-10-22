package com.kswallet.feature.categories.presentation.list

import android.content.Context
import androidx.annotation.PluralsRes
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
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.CategoryType
import com.kswallet.feature.categories.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * The categories screen, rendered and driven like a user would.
 *
 * These run on the JVM (Robolectric hosts the Android runtime), which is the point: an
 * `androidTest` would need an emulator and would therefore never run in this project's
 * CI or on the machine it is developed on. Every assertion below is about the *screen*,
 * not the ViewModel - the tests above it already cover the state machine - so they
 * answer the questions a ViewModel test cannot: does the expense/income tab actually
 * switch, does a row say how many transactions use it, and - the one that matters most
 * for this screen - that deleting cannot happen without the confirmation dialog,
 * because a category in use is not removable without consequences.
 */
class CategoriesScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val intents = mutableListOf<CategoriesAction>()

    @Test
    fun `a list still loading shows a spinner, not the empty state`() {
        show(CategoriesState(isLoading = true))

        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()
        composeRule.onNodeWithText(str(R.string.categories_empty_title)).assertDoesNotExist()
    }

    @Test
    fun `no categories is an empty state that offers the action that fills it`() {
        show(state())

        composeRule.onNodeWithText(str(R.string.categories_empty_title)).assertIsDisplayed()
        composeRule.onNodeWithText(str(R.string.categories_empty_message)).assertIsDisplayed()

        composeRule.onNodeWithContentDescription(str(R.string.categories_add)).performClick()
        assertEquals(listOf(CategoriesAction.AddCategoryClicked), intents)
    }

    @Test
    fun `expenses are the tab the screen opens on`() {
        show(state())

        composeRule.onNodeWithText(str(R.string.category_type_expense)).assert(isSelected())
    }

    @Test
    fun `the income tab switches the list, not just its colour`() {
        show(state())

        composeRule.onNodeWithText(str(R.string.category_type_income)).performClick()
        assertEquals(listOf(CategoriesAction.TypeSelected(CategoryType.INCOME)), intents)
    }

    @Test
    fun `a row says how many transactions use it, and tapping it reports the category`() {
        show(state(categories = listOf(category(id = 5, name = "Groceries", count = 3))))

        composeRule.onNodeWithText("Groceries").assertIsDisplayed()
        composeRule.onNodeWithText(plural(R.plurals.category_transaction_count, 3, 3)).assertIsDisplayed()

        composeRule.onNodeWithText("Groceries").performClick()
        assertEquals(listOf(CategoriesAction.CategoryClicked(CategoryId(5))), intents)
    }

    @Test
    fun `the delete button on a row reports that category`() {
        show(state(categories = listOf(category(id = 2))))

        composeRule.onNodeWithContentDescription(str(R.string.action_delete)).performClick()
        assertEquals(listOf(CategoriesAction.DeleteClicked(CategoryId(2))), intents)
    }

    @Test
    fun `deleting a used category says what happens to its transactions`() {
        show(state(pendingDelete = PendingCategoryDelete(CategoryId(5), "Groceries", 12)))

        composeRule.onNodeWithText(str(R.string.category_delete_title, "Groceries")).assertIsDisplayed()
        composeRule.onNodeWithText(
            plural(R.plurals.category_delete_message_with_transactions, 12, 12),
        ).assertIsDisplayed()

        composeRule.onNodeWithText(str(R.string.action_delete)).performClick()
        assertEquals(listOf(CategoriesAction.DeleteConfirmed), intents)
    }

    @Test
    fun `walking away from the dialog keeps the category`() {
        show(state(pendingDelete = PendingCategoryDelete(CategoryId(5), "Groceries", 0)))

        // Unused categories get the plain message, not the plural one.
        composeRule.onNodeWithText(str(R.string.category_delete_message)).assertIsDisplayed()

        composeRule.onNodeWithText(CancelLabel).performClick()
        assertEquals(listOf(CategoriesAction.DeleteDismissed), intents)
    }

    // --- plumbing ------------------------------------------------------------------------_

    private fun show(state: CategoriesState) {
        composeRule.setContent {
            KsWalletTheme {
                CategoriesScreen(state = state, onAction = { intents += it })
            }
        }
    }

    private fun state(
        categories: List<CategoryRowUi> = emptyList(),
        selectedType: CategoryType = CategoryType.EXPENSE,
        pendingDelete: PendingCategoryDelete? = null,
    ) = CategoriesState(
        isLoading = false,
        selectedType = selectedType,
        categories = categories,
        pendingDelete = pendingDelete,
    )

    private fun category(
        id: Long = 1,
        name: String = "Groceries",
        count: Int = 3,
    ) = CategoryRowUi(
        id = CategoryId(id),
        name = name,
        iconKey = "cart",
        colorArgb = 0xFF112233,
        transactionCount = count,
    )

    /** A tab carries its selection in its semantics. */
    private fun isSelected() =
        SemanticsMatcher.expectValue(SemanticsProperties.Selected, true)

    /** Strings come from the resources, never from English literals in the test. */
    private fun str(@StringRes id: Int, vararg args: Any): String = context.getString(id, *args)

    private fun plural(@PluralsRes id: Int, count: Int, vararg args: Any): String =
        context.resources.getQuantityString(id, count, *args)

    private companion object {
        /** Owned by the design system's dialog, not by this feature. */
        const val CancelLabel = "Cancel"
    }
}
