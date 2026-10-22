package com.kswallet.feature.categories.domain.usecase

import com.kswallet.core.common.result.Outcome
import com.kswallet.core.common.time.TimeProvider
import com.kswallet.core.model.Category
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.CategoryType
import com.kswallet.feature.categories.domain.repository.CategoryRepository
import kotlin.time.Instant

/**
 * Seeds the categories a wallet is unusable without. Idempotent: it only writes when the
 * table is empty, so calling it on every cold start is safe.
 */
class SeedDefaultCategoriesUseCase(
    private val repository: CategoryRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(): Outcome<Unit> {
        val existing = repository.countCategories()
        if (existing is Outcome.Failure) return existing
        if ((existing as Outcome.Success).value > 0) return Outcome.Success(Unit)

        val now = timeProvider.now()
        val defaults = defaultCategories(now)
        return repository.insertDefaultCategories(defaults)
    }

    private fun defaultCategories(now: Instant): List<Category> = listOf(
        default("Groceries", CategoryType.EXPENSE, "groceries", 0xFF1B873F, now),
        default("Restaurants", CategoryType.EXPENSE, "restaurant", 0xFFE07A1F, now),
        default("Transport", CategoryType.EXPENSE, "transport", 0xFF2E6BE6, now),
        default("Rent", CategoryType.EXPENSE, "home", 0xFF5C6B73, now),
        default("Bills", CategoryType.EXPENSE, "bills", 0xFF6A4CE0, now),
        default("Health", CategoryType.EXPENSE, "health", 0xFFC0392B, now),
        default("Shopping", CategoryType.EXPENSE, "shopping", 0xFFB03A8B, now),
        default("Entertainment", CategoryType.EXPENSE, "entertainment", 0xFF0F8B8D, now),
        default("Salary", CategoryType.INCOME, "salary", 0xFF0E7C66, now),
        default("Other income", CategoryType.INCOME, "income", 0xFF1B873F, now),
    )

    private fun default(
        name: String,
        type: CategoryType,
        iconKey: String,
        colorArgb: Long,
        now: Instant,
    ) = Category(
        id = CategoryId.UNSAVED,
        name = name,
        type = type,
        iconKey = iconKey,
        colorArgb = colorArgb,
        createdAt = now,
    )
}
