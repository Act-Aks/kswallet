
package com.kswallet.feature.categories.domain.repository

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.CategoryType
import kotlinx.coroutines.flow.Flow

/**
 * The domain's view of category storage.
 *
 * Streams are `Flow<T>` because reading cannot fail in a way the user can act on;
 * one-shot writes return [Outcome] because they can (validation, conflicts, storage).
 */
interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>

    fun observeCategories(type: CategoryType): Flow<List<Category>>

    /** Category id -> number of transactions. */
    fun observeUsage(): Flow<Map<CategoryId, Int>>

    fun observeCategory(categoryId: CategoryId): Flow<Category?>

    suspend fun getCategory(categoryId: CategoryId): Outcome<Category>

    suspend fun upsertCategory(category: Category): Outcome<CategoryId>

    suspend fun deleteCategory(categoryId: CategoryId): Outcome<Unit>

    suspend fun countCategoriesWithName(type: CategoryType, name: String, excludeId: CategoryId): Outcome<Int>

    suspend fun countCategories(): Outcome<Int>

    suspend fun insertDefaultCategories(categories: List<Category>): Outcome<Unit>
}
