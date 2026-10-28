package com.kswallet.core.database.account

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AccountDao {

    @Query(
        """
        SELECT *
        FROM accounts
        ORDER BY name COLLATE NOCASE ASC, id ASC
        """,
    )
    abstract fun observeAll(): Flow<List<AccountEntity>>

    @Query(
        """
        SELECT *
        FROM accounts
        WHERE id = :id
        LIMIT 1
        """,
    )
    abstract suspend fun findById(id: String): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insert(account: AccountEntity)

    @Query(
        """
        UPDATE accounts
        SET name = :name
        WHERE id = :id
        """,
    )
    abstract suspend fun rename(
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
    abstract suspend fun setArchived(
        id: String,
        archived: Boolean,
    ): Int

    @Transaction
    open suspend fun renameAndRead(
        id: String,
        name: String,
    ): AccountEntity? {
        val affectedRows = rename(
            id = id,
            name = name,
        )

        if (affectedRows == 0) {
            return null
        }

        check(affectedRows == 1) {
            "Renaming an account affected more than one row."
        }

        return checkNotNull(findById(id)) {
            "Account disappeared during rename transaction."
        }
    }

    @Transaction
    open suspend fun setArchivedAndRead(
        id: String,
        archived: Boolean,
    ): AccountEntity? {
        val affectedRows = setArchived(
            id = id,
            archived = archived,
        )

        if (affectedRows == 0) {
            return null
        }

        check(affectedRows == 1) {
            "Updating account status affected more than one row."
        }

        return checkNotNull(findById(id)) {
            "Account disappeared during status transaction."
        }
    }
}