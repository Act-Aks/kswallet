package com.kswallet.feature.categories.presentation.list

import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.CategoryType
import com.kswallet.core.ui.screen.ScreenAction

sealed interface CategoriesAction : ScreenAction {
    data class TypeSelected(val type: CategoryType) : CategoriesAction
    data object AddCategoryClicked : CategoriesAction
    data class CategoryClicked(val id: CategoryId) : CategoriesAction
    data class DeleteClicked(val id: CategoryId) : CategoriesAction
    data object DeleteConfirmed : CategoriesAction
    data object DeleteDismissed : CategoriesAction
}
