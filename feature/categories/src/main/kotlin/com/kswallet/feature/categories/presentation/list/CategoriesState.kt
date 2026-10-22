package com.kswallet.feature.categories.presentation.list

import com.kswallet.core.model.CategoryType
import com.kswallet.core.ui.screen.ScreenState

/**
 * The screen as data: everything the UI needs, nothing it must ask for.
 *
 * `CategoriesUiState` is the *only* thing the composable renders, which is what makes the
 * screen trivially previewable and the ViewModel fully testable without a device.
 */
data class CategoriesState(
    val isLoading: Boolean = true,
    val selectedType: CategoryType = CategoryType.EXPENSE,
    val categories: List<CategoryRowUi> = emptyList(),
    val pendingDelete: PendingCategoryDelete? = null,
) : ScreenState {
    val isEmpty: Boolean get() = !isLoading && categories.isEmpty()
}
