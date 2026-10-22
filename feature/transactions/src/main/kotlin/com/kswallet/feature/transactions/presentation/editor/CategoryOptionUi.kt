package com.kswallet.feature.transactions.presentation.editor

import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.CategoryType

data class CategoryOptionUi(
    val id: CategoryId,
    val name: String,
    val type: CategoryType,
    val iconKey: String,
)
