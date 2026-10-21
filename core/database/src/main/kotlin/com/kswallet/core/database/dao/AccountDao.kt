package com.kswallet.core.database.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.kswallet.core.database.entity.AccountEntity
import com.kswallet.core.database.model.AccountWithBalance
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    /**
     * Balance = initial balance + income - expense + incoming transfers - outgoing transfers.
     *
     * Business rule: a transfer moves money without creating or destroying any, so it
     * subtracts from the source account and adds to the target account.
     */
    @Query(
        """
        SELECT a.*,
            a.initial_balance_minor
            + COALESCE((
                SELECT SUM(CASE WHEN t.type = 'INCOME' THEN t.amount_minor ELSE -t.amount_minor END)
                FROM transactions t
                WHERE t.account_id = a.id AND t.type IN ('INCOME', 'EXPENSE')
            ), 0)
            + COALESCE((
                SELECT SUM(t.amount_minor) FROM transactions t
                WHERE t.type = 'TRANSFER' AND t.transfer_account_id = a.id
            ), 0)
            - COALESCE((
                SELECT SUM(t.amount_minor) FROM transactions t
                WHERE t.type = 'TRANSFER' AND t.account_id = a.id
            ), 0)
            AS balance_minor
        FROM accounts a
        WHERE a.is_archived = 0
        ORDER BY a.created_at ASC
        """,
    )
    fun observeAccountsWithBalance(): Flow<List<AccountWithBalance>>

    @Query(
        """
        SELECT a.*,
            a.initial_balance_minor
            + COALESCE((
                SELECT SUM(CASE WHEN t.type = 'INCOME' THEN t.amount_minor ELSE -t.amount_minor END)
                FROM transactions t
                WHERE t.account_id = a.id AND t.type IN ('INCOME', 'EXPENSE')
            ), 0)
            + COALESCE((
                SELECT SUM(t.amount_minor) FROM transactions t
                WHERE t.type = 'TRANSFER' AND t.transfer_account_id = a.id
            ), 0)
            - COALESCE((
                SELECT SUM(t.amount_minor) FROM transactions t
                WHERE t.type = 'TRANSFER' AND t.account_id = a.id
            ), 0)
            AS balance_minor
        FROM accounts a
        WHERE a.id = :accountId
        """,
    )
    fun observeAccountWithBalance(accountId: Long): Flow<AccountWithBalance?>

    @Query("SELECT * FROM accounts WHERE id = :accountId")
    suspend fun findById(accountId: Long): AccountEntity?

    /** Currency of one account - lets a feature map a ledger row without joining accounts. */
    @Query("SELECT currency_code FROM accounts WHERE id = :accountId")
    suspend fun findCurrencyCode(accountId: Long): String?

    @Query("SELECT * FROM accounts WHERE is_archived = 0 ORDER BY created_at ASC")
    suspend fun getAll(): List<AccountEntity>

    @Insert
    suspend fun insert(account: AccountEntity): Long

    @Update
    suspend fun update(account: AccountEntity)

    @Delete
    suspend fun delete(account: AccountEntity)

    /**
     * Total of every account that is included in the total, in one currency.
     * Currencies are never summed together - there is no exchange rate in the app.
     */
    @Query(
        """
        SELECT COALESCE(SUM(
            a.initial_balance_minor
            + COALESCE((
                SELECT SUM(CASE WHEN t.type = 'INCOME' THEN t.amount_minor ELSE -t.amount_minor END)
                FROM transactions t
                WHERE t.account_id = a.id AND t.type IN ('INCOME', 'EXPENSE')
            ), 0)
            + COALESCE((
                SELECT SUM(t.amount_minor) FROM transactions t
                WHERE t.type = 'TRANSFER' AND t.transfer_account_id = a.id
            ), 0)
            - COALESCE((
                SELECT SUM(t.amount_minor) FROM transactions t
                WHERE t.type = 'TRANSFER' AND t.account_id = a.id
            ), 0)
        ), 0)
        FROM accounts a
        WHERE a.is_archived = 0 AND a.included_in_total = 1 AND a.currency_code = :currencyCode
        """,
    )
    fun observeTotalBalance(currencyCode: String): Flow<Long>

    /** Used to warn before deleting an account: deleting it deletes this many entries. */
    @Query("SELECT COUNT(*) FROM transactions WHERE account_id = :accountId OR transfer_account_id = :accountId")
    suspend fun countTransactionsFor(accountId: Long): Int

    @Query("SELECT COUNT(*) FROM accounts WHERE is_archived = 0")
    suspend fun countAccounts(): Int

    @Query("SELECT COUNT(DISTINCT currency_code) FROM accounts WHERE is_archived = 0")
    suspend fun countCurrencies(): Int
}
