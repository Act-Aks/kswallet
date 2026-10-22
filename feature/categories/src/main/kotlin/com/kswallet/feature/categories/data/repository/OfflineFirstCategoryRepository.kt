package com.kswallet.feature.categories.data.repository

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.common.result.map
import com.kswallet.core.common.result.outcomeOfSuspend
import com.kswallet.core.common.result.outcomeOf
import com.kswallet.core.database.dao.CategoryDao
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.CategoryType
import com.kswallet.feature.categories.data.mapper.toDomain
import com.kswallet.feature.categories.data.mapper.toEntity
import com.kswallet.feature.categories.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The repository is deliberately thin: it maps, it guards, it forwards to the DAO.
 * Anything smarter belongs in a use case, where it can be tested without a database.
 */
class OfflineFirstCategoryRepository(
    private val categoryDao: CategoryDao,
) : CategoryRepository {

    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeCategories().map { entities -> entities.map { it.toDomain() } }

    override fun observeCategories(type: CategoryType): Flow<List<Category>> =
        categoryDao.observeCategoriesByType(type.name).map { entities -> entities.map { it.toDomain() } }

    override fun observeUsage(): Flow<Map<CategoryId, Int>> =
        categoryDao.observeUsage().map { rows -> rows.associate { CategoryId(it.categoryId) to it.transactionCount } }

    override fun observeCategory(categoryId: CategoryId): Flow<Category?> =
        categoryDao.observeCategories().map { entities ->
            entities.firstOrNull { it.id == categoryId.value }?.toDomain()
        }

    override suspend fun getCategory(categoryId: CategoryId): Outcome<Category> =
        outcomeOfSuspend {
            categoryDao.findById(categoryId.value)?.toDomain()
        }.map { it ?: throw NoSuchElementException("Category ${categoryId.value} not found") }

    override suspend fun upsertCategory(category: Category): Outcome<CategoryId> = outcomeOfSuspend {
        if (category.id.isSaved) {
            categoryDao.update(category.toEntity())
            category.id
        } else {
            CategoryId(categoryDao.insert(category.toEntity()))
        }
    }

    override suspend fun deleteCategory(categoryId: CategoryId): Outcome<Unit> = outcomeOfSuspend {
        val entity = categoryDao.findById(categoryId.value)
            ?: throw NoSuchElementException("Category ${categoryId.value} not found")
        categoryDao.delete(entity)
    }

    override suspend fun countCategoriesWithName(
        type: CategoryType,
        name: String,
        excludeId: CategoryId,
    ): Outcome<Int> = outcomeOfSuspend {
        categoryDao.countWithSameName(type.name, name, excludeId.value)
    }

    override suspend fun countCategories(): Outcome<Int> = outcomeOfSuspend { categoryDao.countCategories() }

    override suspend fun insertDefaultCategories(categories: List<Category>): Outcome<Unit> = outcomeOfSuspend {
        categoryDao.insertAll(categories.map { it.toEntity() })
    }
}
