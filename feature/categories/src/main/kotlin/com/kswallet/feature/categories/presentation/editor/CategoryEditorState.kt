package com.kswallet.feature.categories.presentation.editor

import com.kswallet.core.model.CategoryType
import com.kswallet.core.ui.screen.ScreenState

data class CategoryEditorState(
    val isLoading: Boolean = true,
    val isNew: Boolean = true,
    val name: String = "",
    val type: CategoryType = CategoryType.EXPENSE,
    val iconKey: String = "category",
    val colorArgb: Long = 0,
    val error: CategoryEditorError? = null,
    val isSaving: Boolean = false,
) : ScreenState
