package com.kswallet.core.database.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.kswallet.core.database.entity.CategoryEntity
import com.kswallet.core.database.model.CategoryUsageRow
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY type ASC, name COLLATE NOCASE ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name COLLATE NOCASE ASC")
    fun observeCategoriesByType(type: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun findById(categoryId: Long): CategoryEntity?

    @Query(
        """
        SELECT COUNT(*) FROM categories
        WHERE type = :type AND name = :name COLLATE NOCASE AND id != :excludeId
        """,
    )
    suspend fun countWithSameName(type: String, name: String, excludeId: Long): Int

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun countCategories(): Int

    @Query(
        """
        SELECT c.id AS category_id, COUNT(t.id) AS transaction_count
        FROM categories c LEFT JOIN transactions t ON t.category_id = c.id
        GROUP BY c.id
        """,
    )
    fun observeUsage(): Flow<List<CategoryUsageRow>>

    @Insert
    suspend fun insert(category: CategoryEntity): Long

    @Insert
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)
}
