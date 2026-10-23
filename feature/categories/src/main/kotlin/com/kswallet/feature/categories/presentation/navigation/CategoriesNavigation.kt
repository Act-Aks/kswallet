package com.kswallet.feature.categories.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.core.navigation.KsWalletDestination
import com.kswallet.feature.categories.presentation.editor.CategoryEditorRoute
import com.kswallet.feature.categories.presentation.list.CategoriesRoute
import kotlinx.serialization.Serializable

/** Destinations owned by the categories feature. */
@Serializable
data object CategoriesDestination : KsWalletDestination

@Serializable
data class CategoryEditorDestination(val categoryId: Long? = null) : KsWalletDestination

/**
 * The feature's whole public navigation surface: the app (or another feature) can reach
 * every screen without knowing any composable of this module.
 */
fun EntryProviderScope<NavKey>.categoriesEntries(
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (categoryId: Long?) -> Unit,
) {
    entry<CategoriesDestination> {
        CategoriesRoute(
            onAddCategory = { onNavigateToEditor(null) },
            onEditCategory = { categoryId -> onNavigateToEditor(categoryId.value) },
        )
    }

    entry<CategoryEditorDestination> { destination ->
        CategoryEditorRoute(
            categoryIdValue = destination.categoryId ?: 0L,
            onNavigateBack = onNavigateBack,
        )
    }
}
