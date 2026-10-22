package com.kswallet.feature.budgets.data.repository

import com.kswallet.core.common.result.DomainError
import com.kswallet.core.common.result.Outcome
import com.kswallet.core.common.result.outcomeOfSuspend
import com.kswallet.core.common.time.TimeProvider
import com.kswallet.core.database.dao.BudgetDao
import com.kswallet.core.database.model.CategorySpendRow
import com.kswallet.core.model.BudgetId
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.Currency
import com.kswallet.core.model.Money
import com.kswallet.feature.budgets.data.mapper.BudgetMapper
import com.kswallet.feature.budgets.domain.model.Budget
import com.kswallet.feature.budgets.domain.model.BudgetPeriod
import com.kswallet.feature.budgets.domain.model.BudgetProgress
import com.kswallet.feature.budgets.domain.repository.BudgetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Budgets, backed by the ledger.
 *
 * `spent` is never stored: it is the sum of the category's expenses inside the period, asked
 * of SQL (which has the index for it) and looked up per currency, so a budget in rupees never
 * absorbs spending from a dollar account.
 *
 * The three windows (week, month, year) are computed once per subscription from the injected
 * clock, and the flows are re-subscribed when the day changes - which is what makes a budget
 * quietly roll over to the new month without the process being restarted.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFirstBudgetRepository(
    private val budgetDao: BudgetDao,
    private val timeProvider: TimeProvider,
) : BudgetRepository {

    override fun observeBudgets(): Flow<List<BudgetProgress>> =
        windowsFlow().flatMapLatest { windows ->
            combine(
                budgetDao.observeBudgetsWithCategory(),
                budgetDao.observeCategorySpend(
                    from = windows.getValue(BudgetPeriod.WEEKLY).start.toEpochMilliseconds(),
                    to = windows.getValue(BudgetPeriod.WEEKLY).endExclusive.toEpochMilliseconds(),
                ),
                budgetDao.observeCategorySpend(
                    from = windows.getValue(BudgetPeriod.MONTHLY).start.toEpochMilliseconds(),
                    to = windows.getValue(BudgetPeriod.MONTHLY).endExclusive.toEpochMilliseconds(),
                ),
                budgetDao.observeCategorySpend(
                    from = windows.getValue(BudgetPeriod.YEARLY).start.toEpochMilliseconds(),
                    to = windows.getValue(BudgetPeriod.YEARLY).endExclusive.toEpochMilliseconds(),
                ),
            ) { rows, weekly, monthly, yearly ->
                val spend = SpendIndex(weekly, monthly, yearly)
                rows.map { row ->
                    val entity = BudgetMapper.toDomain(row.budget)
                    BudgetMapper.toProgress(row, spend.of(entity.categoryId, entity.limit.currency, entity.period))
                }
            }
        }

    override fun observeBudget(budgetId: BudgetId): Flow<Budget?> = flowOf(budgetId)
        .map { budgetDao.findById(it.value) }
        .map { entity -> entity?.let(BudgetMapper::toDomain) }
        .distinctUntilChanged()

    override suspend fun getBudget(budgetId: BudgetId): Outcome<Budget> {
        val entity = budgetDao.findById(budgetId.value) ?: return Outcome.Failure(DomainError.NotFound)
        return Outcome.Success(BudgetMapper.toDomain(entity))
    }

    override suspend fun upsertBudget(budget: Budget): Outcome<BudgetId> = outcomeOfSuspend {
        val entity = BudgetMapper.toEntity(budget, timeProvider.now())
        val id = if (budget.id.isSaved) {
            budgetDao.update(entity)
            budget.id
        } else {
            BudgetId(budgetDao.insert(entity))
        }
        id
    }

    override suspend fun deleteBudget(budgetId: BudgetId): Outcome<Unit> = outcomeOfSuspend {
        val entity = budgetDao.findById(budgetId.value) ?: error("budget ${budgetId.value} does not exist")
        budgetDao.delete(entity)
        Unit
    }

    /**
     * The current windows, re-emitted when the calendar day changes.
     *
     * `today()` is polled on every subscription and compared with the previous value, so the
     * flow stays cold (no timers left running) and still rolls over for a session that is left
     * open overnight.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun windowsFlow(): Flow<Map<BudgetPeriod, com.kswallet.core.common.time.DateRange>> =
        flowOf(timeProvider.today())
            .map { today -> BudgetPeriod.entries.associateWith { it.currentRange(timeProvider) } }
            .distinctUntilChanged()

    /** Spend per (category, currency) per period, so a lookup is a map read and not a scan. */
    private class SpendIndex(
        private val weekly: List<CategorySpendRow>,
        private val monthly: List<CategorySpendRow>,
        private val yearly: List<CategorySpendRow>,
    ) {
        fun of(categoryId: CategoryId, currency: Currency, period: BudgetPeriod): Money {
            val rows = when (period) {
                BudgetPeriod.WEEKLY -> weekly
                BudgetPeriod.MONTHLY -> monthly
                BudgetPeriod.YEARLY -> yearly
            }
            val minor = rows.firstOrNull { it.categoryId == categoryId.value && it.currencyCode == currency.code }
                ?.spentMinor
                ?: 0L
            return Money(minor, currency)
        }
    }
}
