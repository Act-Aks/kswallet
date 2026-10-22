package com.kswallet.feature.categories.domain.model

import com.kswallet.core.model.Category

/**
 * A category plus how many transactions use it - the list screen needs both, and the
 * database can produce both in one pass, so the combination happens in the domain.
 */
data class CategorySummary(
    val category: Category,
    val transactionCount: Int,
)
