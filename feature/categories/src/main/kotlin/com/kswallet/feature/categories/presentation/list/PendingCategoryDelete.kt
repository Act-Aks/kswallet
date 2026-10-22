package com.kswallet.feature.categories.presentation.list

import com.kswallet.core.model.CategoryId

data class PendingCategoryDelete(
    val id: CategoryId,
    val name: String,
    val transactionCount: Int,
)
