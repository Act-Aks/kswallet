package com.kswallet.core.database.account

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query(
        """
        SELECT *
        FROM accounts
        ORDER BY name COLLATE NOCASE ASC, id ASC
        """,
    )
    fun observeAll(): Flow<List<AccountEntity>>

    @Query(
        """
        SELECT *
        FROM accounts
        WHERE id = :id
        LIMIT 1
        """,
    )
    suspend fun findById(id: String): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(account: AccountEntity)

    @Query(
        """
        UPDATE accounts
        SET name = :name
        WHERE id = :id
        """,
    )
    suspend fun rename(
        id: String,
        name: String,
    ): Int

    @Query(
        """
        UPDATE accounts
        SET archived = :archived
        WHERE id = :id
        """,
    )
    suspend fun setArchived(
        id: String,
        archived: Boolean,
    ): Int
}