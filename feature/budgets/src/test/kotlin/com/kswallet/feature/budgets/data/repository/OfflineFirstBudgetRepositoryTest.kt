package com.kswallet.feature.budgets.data.repository

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.database.dao.BudgetDao
import com.kswallet.core.database.entity.BudgetEntity
import com.kswallet.core.database.model.BudgetWithCategoryRow
import com.kswallet.core.database.model.CategorySpendRow
import com.kswallet.core.model.BudgetId
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.core.testing.FixedTimeProvider
import com.kswallet.feature.budgets.domain.model.Budget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.ExperimentalTime

/**
 * The repository's job is the *join*: which window each budget counts, and which currency's
 * spending it picks up. Both are strings of SQL in production, so the test fakes the DAO and
 * checks the mapping the repository does around it - including the two rules that are easy to
 * get wrong: a budget counts only its own period's window, and only spending in its own
 * currency.
 */
@OptIn(ExperimentalTime::class)
class OfflineFirstBudgetRepositoryTest {

    private val currency = Currency.INR
    private val zone = TimeZone.of("Asia/Kolkata")
    private val timeProvider = FixedTimeProvider.of("2026-10-12", zone)

    private val budgets = MutableStateFlow(listOf(row(1, period = "MONTHLY")))

    /** Every spend query the repository makes, keyed by the window it asked for. */
    private class FakeBudgetDao(
        private val budgets: MutableStateFlow<List<BudgetWithCategoryRow>>,
        private val spendByWindow: (Long, Long) -> List<CategorySpendRow>,
    ) : BudgetDao {
        val requestedWindows = mutableListOf<Pair<Long, Long>>()
        private val stored = mutableListOf<BudgetEntity>()
        private var nextId = 100L

        override fun observeBudgetsWithCategory(): Flow<List<BudgetWithCategoryRow>> = budgets

        override suspend fun findById(budgetId: Long): BudgetEntity? =
            stored.firstOrNull { it.id == budgetId } ?: budgets.value.firstOrNull { it.budget.id == budgetId }?.budget

        override suspend fun findForCategory(categoryId: Long, period: String): BudgetEntity? =
            stored.firstOrNull { it.categoryId == categoryId && it.period == period }

        override fun observeCategorySpend(from: Long, to: Long): Flow<List<CategorySpendRow>> {
            requestedWindows += from to to
            return budgets.map { spendByWindow(from, to) }
        }

        override suspend fun countBudgets(): Int = budgets.value.size

        override suspend fun insert(budget: BudgetEntity): Long {
            val id = nextId++
            stored += budget.copy(id = id)
            return id
        }

        override suspend fun update(budget: BudgetEntity) {
            stored.removeAll { it.id == budget.id }
            stored += budget
        }

        override suspend fun delete(budget: BudgetEntity) {
            stored.removeAll { it.id == budget.id }
        }
    }

    private fun row(id: Long, period: String, categoryId: Long = 10) = BudgetWithCategoryRow(
        budget = BudgetEntity(
            id = id,
            categoryId = categoryId,
            limitMinor = 100_000,
            currencyCode = currency.code,
            period = period,
            createdAt = 0L,
        ),
        categoryName = "Groceries",
        categoryIconKey = "groceries",
        categoryColorArgb = 0xFF112233,
    )

    /** 400.00 in INR for the month window, 900.00 for the year, nothing for the week. */
    private fun spendFor(monthStart: Long, monthEnd: Long): List<CategorySpendRow> = when {
        monthEnd - monthStart > 300L * 24 * 3600 * 1000 -> listOf(CategorySpendRow(10, "INR", 90_000))
        monthEnd - monthStart < 8L * 24 * 3600 * 1000 -> emptyList()
        else -> listOf(CategorySpendRow(10, "INR", 40_000))
    }

    private fun repository(dao: BudgetDao) = OfflineFirstBudgetRepository(dao, timeProvider)

    @Test
    fun `a monthly budget counts the month window, not the week or the year`() = runTest {
        val dao = FakeBudgetDao(budgets, ::spendFor)
        val progress = repository(dao).observeBudgets().first()

        assertEquals(1, progress.size)
        assertEquals(Money.of("400.00", currency), progress.first().spent)
        assertEquals(3, dao.requestedWindows.size)
    }

    @Test
    fun `a yearly budget counts the year window`() = runTest {
        budgets.value = listOf(row(2, period = "YEARLY"))
        val progress = repository(FakeBudgetDao(budgets, ::spendFor)).observeBudgets().first()

        assertEquals(Money.of("900.00", currency), progress.first().spent)
    }

    @Test
    fun `a weekly budget counts the week window`() = runTest {
        budgets.value = listOf(row(3, period = "WEEKLY"))
        val progress = repository(FakeBudgetDao(budgets, ::spendFor)).observeBudgets().first()

        assertEquals(Money.zero(currency), progress.first().spent)
    }

    @Test
    fun `spending in another currency is not counted against the budget`() = runTest {
        val dao = FakeBudgetDao(budgets) { from, to ->
            // The same category, spent in dollars only.
            if (to - from > 300L * 24 * 3600 * 1000) emptyList() else listOf(CategorySpendRow(10, "USD", 40_000))
        }

        val progress = repository(dao).observeBudgets().first()

        assertEquals(Money.zero(currency), progress.first().spent)
        assertFalse(progress.first().isOverBudget)
    }

    @Test
    fun `the progress carries the category and the clamped fraction`() = runTest {
        budgets.value = listOf(row(4, period = "MONTHLY"))
        val dao = FakeBudgetDao(budgets) { from, to ->
            if (to - from < 8L * 24 * 3600 * 1000) emptyList() else listOf(CategorySpendRow(10, "INR", 250_000))
        }

        val progress = repository(dao).observeBudgets().first().single()

        assertEquals("Groceries", progress.category.name)
        assertTrue(progress.isOverBudget)
        assertEquals(1f, progress.fractionUsed, 0.0001f)
        assertEquals(Money.of("1500.00", currency), progress.spent - progress.budget.limit)
    }

    @Test
    fun `saving a new budget inserts it and reports the id`() = runTest {
        val dao = FakeBudgetDao(budgets, ::spendFor)

        val result = repository(dao).upsertBudget(
            Budget(categoryId = CategoryId(10), limit = Money.of("1000.00", currency)),
        )

        assertTrue(result is Outcome.Success)
        assertEquals(BudgetId(100), (result as Outcome.Success).value)
    }

    @Test
    fun `saving an existing budget keeps its id`() = runTest {
        val dao = FakeBudgetDao(budgets, ::spendFor)
        val repository = repository(dao)
        val saved = repository.upsertBudget(
            Budget(categoryId = CategoryId(10), limit = Money.of("1000.00", currency)),
        ) as Outcome.Success

        val updated = repository.upsertBudget(
            Budget(id = saved.value, categoryId = CategoryId(10), limit = Money.of("2000.00", currency)),
        )

        assertEquals(saved.value, (updated as Outcome.Success).value)
    }

    @Test
    fun `deleting a budget that is gone fails instead of throwing`() = runTest {
        val result = repository(FakeBudgetDao(budgets, ::spendFor)).deleteBudget(BudgetId(999))

        assertTrue(result is Outcome.Failure)
    }

    @Test
    fun `per-currency sums reach the budget in its own currency`() = runTest {
        // Two sums for the same category in different currencies: only the matching one counts.
        val dao = FakeBudgetDao(budgets) { from, to ->
            if (to - from < 8L * 24 * 3600 * 1000) {
                emptyList()
            } else {
                listOf(CategorySpendRow(10, "USD", 999_999), CategorySpendRow(10, "INR", 12_345))
            }
        }

        val progress = repository(dao).observeBudgets().first().single()

        assertEquals(Money.of("123.45", currency), progress.spent)
    }
}
