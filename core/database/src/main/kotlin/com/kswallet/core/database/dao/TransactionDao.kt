package com.kswallet.core.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.kswallet.core.database.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TransactionDao {

    @Query(
        """
        SELECT *
        FROM transactions
        WHERE account_id = :accountId
        ORDER BY timestamp_epoch_millis DESC, id ASC
        """,
    )
    abstract fun observeByAccount(accountId: String): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT *
        FROM transactions
        WHERE id = :id
        LIMIT 1
        """,
    )
    abstract suspend fun findById(id: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insert(transaction: TransactionEntity)

    @Query(
        """
        DELETE FROM transactions
        WHERE id = :id
        """,
    )
    abstract suspend fun deleteById(id: String): Int

    @Transaction
    open suspend fun insertAndRead(transaction: TransactionEntity): TransactionEntity {
        insert(transaction)

        return checkNotNull(findById(transaction.id)) {
            "Transaction disappeared after insertion."
        }
    }
}