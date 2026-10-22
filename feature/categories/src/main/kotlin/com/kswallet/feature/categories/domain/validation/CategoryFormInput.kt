package com.kswallet.feature.categories.domain.validation

import com.kswallet.core.model.CategoryType

/** What the editor screen collects. Validation runs on this, not on the UI state. */
data class CategoryFormInput(
    val name: String,
    val type: CategoryType,
    val iconKey: String,
    val colorArgb: Long,
)
