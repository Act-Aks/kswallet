package com.kswallet.feature.categories.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kswallet.core.navigation.KsDestination
import com.kswallet.feature.categories.presentation.editor.CategoryEditorRoute
import com.kswallet.feature.categories.presentation.list.CategoriesRoute
import kotlinx.serialization.Serializable

/** Destinations owned by the categories feature. */
@Serializable
data object CategoriesDestination : KsDestination

@Serializable
data class CategoryEditorDestination(val categoryId: Long? = null) : KsDestination

/**
 * The feature's whole public navigation surface: the app (or another feature) can reach
 * every screen without knowing any composable of this module.
 */
fun NavGraphBuilder.categoriesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (categoryId: Long?) -> Unit,
) {
    composable<CategoriesDestination> {
        CategoriesRoute(
            onAddCategory = { onNavigateToEditor(null) },
            onEditCategory = { categoryId -> onNavigateToEditor(categoryId.value) },
        )
    }

    composable<CategoryEditorDestination> { backStackEntry ->
        val destination = backStackEntry.toRoute<CategoryEditorDestination>()
        CategoryEditorRoute(
            categoryIdValue = destination.categoryId ?: 0L,
            onNavigateBack = onNavigateBack,
        )
    }
}
