package com.kswallet.core.database.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.kswallet.core.database.entity.BudgetEntity
import com.kswallet.core.database.model.BudgetWithCategoryRow
import com.kswallet.core.database.model.CategorySpendRow
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    /** Budgets joined with the category they limit - the list screen renders both. */
    @Query(
        """
        SELECT b.*,
            c.name AS category_name,
            c.icon_key AS category_icon_key,
            c.color_argb AS category_color_argb
        FROM budgets b
        JOIN categories c ON c.id = b.category_id
        ORDER BY c.name COLLATE NOCASE ASC
        """,
    )
    fun observeBudgetsWithCategory(): Flow<List<BudgetWithCategoryRow>>

    @Query("SELECT * FROM budgets WHERE id = :budgetId")
    suspend fun findById(budgetId: Long): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE category_id = :categoryId AND period = :period")
    suspend fun findForCategory(categoryId: Long, period: String): BudgetEntity?

    /**
     * What was spent per category - and per currency - inside one window.
     *
     * The currency comes from the *account the money left*, not from the budget: a budget in
     * rupees must not count spending from a dollar account, because adding those two numbers
     * would need an exchange rate. The repository looks a budget's own currency up in this
     * result, which is why the sum is grouped by both columns.
     */
    @Query(
        """
        SELECT t.category_id AS category_id,
            a.currency_code AS currency_code,
            COALESCE(SUM(t.amount_minor), 0) AS spent_minor
        FROM transactions t
        JOIN accounts a ON a.id = t.account_id
        WHERE t.type = 'EXPENSE'
            AND t.category_id IS NOT NULL
            AND t.date_time >= :from AND t.date_time < :to
        GROUP BY t.category_id, a.currency_code
        """,
    )
    fun observeCategorySpend(from: Long, to: Long): Flow<List<CategorySpendRow>>

    @Query("SELECT COUNT(*) FROM budgets")
    suspend fun countBudgets(): Int

    @Insert
    suspend fun insert(budget: BudgetEntity): Long

    @Update
    suspend fun update(budget: BudgetEntity)

    @Delete
    suspend fun delete(budget: BudgetEntity)
}
