package com.kswallet.feature.categories.presentation.list

import com.kswallet.core.model.CategoryId

data class CategoryRowUi(
    val id: CategoryId,
    val name: String,
    val iconKey: String,
    val colorArgb: Long,
    val transactionCount: Int,
)
