package com.kswallet.core.database.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.kswallet.core.database.entity.TransactionEntity
import com.kswallet.core.database.model.CashFlowPointRow
import com.kswallet.core.database.model.CashFlowRow
import com.kswallet.core.database.model.CategorySpendWithDetailsRow
import com.kswallet.core.database.model.TransactionWithDetailsRow
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    /**
     * The one query that powers the list screen. Every filter is optional: passing null
     * for a parameter means "do not filter by it", which keeps the SQL (and its index
     * usage) in a single place instead of one query per filter combination.
     */
    @Query(
        """
        SELECT t.*,
            a.name AS account_name,
            a.currency_code AS account_currency,
            ta.name AS transfer_account_name,
            c.name AS category_name,
            c.icon_key AS category_icon_key,
            c.color_argb AS category_color_argb
        FROM transactions t
        LEFT JOIN accounts a ON a.id = t.account_id
        LEFT JOIN accounts ta ON ta.id = t.transfer_account_id
        LEFT JOIN categories c ON c.id = t.category_id
        WHERE t.date_time >= :from AND t.date_time < :to
            AND (:type IS NULL OR t.type = :type)
            AND (:accountId IS NULL OR t.account_id = :accountId OR t.transfer_account_id = :accountId)
            AND (:categoryId IS NULL OR t.category_id = :categoryId)
        ORDER BY t.date_time DESC, t.id DESC
        LIMIT :limit
        """,
    )
    fun observeTransactions(
        from: Long,
        to: Long,
        type: String?,
        accountId: Long?,
        categoryId: Long?,
        limit: Int,
    ): Flow<List<TransactionWithDetailsRow>>

    @Query(
        """
        SELECT t.*,
            a.name AS account_name,
            a.currency_code AS account_currency,
            ta.name AS transfer_account_name,
            c.name AS category_name,
            c.icon_key AS category_icon_key,
            c.color_argb AS category_color_argb
        FROM transactions t
        LEFT JOIN accounts a ON a.id = t.account_id
        LEFT JOIN accounts ta ON ta.id = t.transfer_account_id
        LEFT JOIN categories c ON c.id = t.category_id
        ORDER BY t.date_time DESC, t.id DESC
        LIMIT :limit
        """,
    )
    fun observeRecentTransactions(limit: Int): Flow<List<TransactionWithDetailsRow>>

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun findById(transactionId: Long): TransactionEntity?

    /** Income/expense totals of a period for the accounts of one currency (transfers excluded). */
    @Query(
        """
        SELECT
            COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount_minor ELSE 0 END), 0) AS income_minor,
            COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount_minor ELSE 0 END), 0) AS expense_minor
        FROM transactions t
        JOIN accounts a ON a.id = t.account_id
        WHERE t.date_time >= :from AND t.date_time < :to
            AND a.currency_code = :currencyCode
            AND t.type IN ('INCOME', 'EXPENSE')
        """,
    )
    fun observeCashFlow(from: Long, to: Long, currencyCode: String): Flow<CashFlowRow>

    /**
     * Spending per category, for the accounts of one currency (transfers and income excluded).
     *
     * `LEFT JOIN` on purpose: transactions with no category are one more row (id `0`) rather
     * than a silent gap, so the rows of a report always add up to the period's total.
     */
    @Query(
        """
        SELECT
            COALESCE(t.category_id, 0) AS category_id,
            c.name AS category_name,
            c.icon_key AS category_icon_key,
            c.color_argb AS category_color_argb,
            COUNT(t.id) AS transaction_count,
            COALESCE(SUM(t.amount_minor), 0) AS spent_minor
        FROM transactions t
        JOIN accounts a ON a.id = t.account_id
        LEFT JOIN categories c ON c.id = t.category_id
        WHERE t.date_time >= :from AND t.date_time < :to
            AND a.currency_code = :currencyCode
            AND t.type = 'EXPENSE'
        GROUP BY COALESCE(t.category_id, 0)
        ORDER BY spent_minor DESC
        """,
    )
    fun observeCategorySpendWithDetails(
        from: Long,
        to: Long,
        currencyCode: String,
    ): Flow<List<CategorySpendWithDetailsRow>>

    /** Every instant of the window that moved money, with its income and expense sums. */
    @Query(
        """
        SELECT
            t.date_time AS occurred_at,
            COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount_minor ELSE 0 END), 0) AS income_minor,
            COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount_minor ELSE 0 END), 0) AS expense_minor
        FROM transactions t
        JOIN accounts a ON a.id = t.account_id
        WHERE t.date_time >= :from AND t.date_time < :to
            AND a.currency_code = :currencyCode
            AND t.type IN ('INCOME', 'EXPENSE')
        GROUP BY t.date_time
        ORDER BY t.date_time
        """,
    )
    fun observeCashFlowPoints(from: Long, to: Long, currencyCode: String): Flow<List<CashFlowPointRow>>

    @Query("SELECT COUNT(*) FROM transactions WHERE category_id = :categoryId")
    suspend fun countByCategory(categoryId: Long): Int

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun countTransactions(): Int

    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)
}
