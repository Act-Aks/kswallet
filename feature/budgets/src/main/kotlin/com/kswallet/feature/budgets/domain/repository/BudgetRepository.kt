package com.kswallet.feature.budgets.domain.repository

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.model.BudgetId
import com.kswallet.feature.budgets.domain.model.Budget
import com.kswallet.feature.budgets.domain.model.BudgetProgress
import kotlinx.coroutines.flow.Flow

/**
 * The contract the budgets feature is written against.
 *
 * `spent` is computed, not stored: the repository reads the budgets and the ledger and
 * combines them, so a budget can never show a total that disagrees with the transactions
 * behind it.
 */
interface BudgetRepository {
    fun observeBudgets(): Flow<List<BudgetProgress>>

    /** The editor loads one budget; a missing id emits `null` instead of failing. */
    fun observeBudget(budgetId: BudgetId): Flow<Budget?>

    suspend fun getBudget(budgetId: BudgetId): Outcome<Budget>

    suspend fun upsertBudget(budget: Budget): Outcome<BudgetId>

    suspend fun deleteBudget(budgetId: BudgetId): Outcome<Unit>
}
