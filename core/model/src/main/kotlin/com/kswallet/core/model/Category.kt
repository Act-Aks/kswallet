package com.kswallet.core.model

import kotlin.time.Instant

data class Category(
    val id: CategoryId,
    val name: String,
    val type: CategoryType,
    val iconKey: String,
    val colorArgb: Long,
    val createdAt: Instant,
) {
    val isPersisted: Boolean get() = id.isSaved
}

enum class CategoryType {
    INCOME,
    EXPENSE,
    ;

    companion object {
        fun forTransactionType(type: TransactionType): CategoryType? = when (type) {
            TransactionType.INCOME -> INCOME
            TransactionType.EXPENSE -> EXPENSE
            TransactionType.TRANSFER -> null
        }
    }
}
