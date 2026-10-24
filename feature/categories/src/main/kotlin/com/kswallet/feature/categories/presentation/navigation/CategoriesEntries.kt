package com.kswallet.feature.categories.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kswallet.feature.categories.presentation.editor.CategoryEditorRoot
import com.kswallet.feature.categories.presentation.list.CategoriesRoot

/**
 * The feature's whole public navigation surface: the app (or another feature) can reach
 * every screen without knowing any composable of this module.
 */
fun EntryProviderScope<NavKey>.categoriesEntries(
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (categoryId: Long?) -> Unit,
) {
    entry<CategoriesRoute> {
        CategoriesRoot(
            onAddCategory = { onNavigateToEditor(null) },
            onEditCategory = { categoryId -> onNavigateToEditor(categoryId.value) },
        )
    }

    entry<CategoryEditorRoute> { destination ->
        CategoryEditorRoot(
            categoryIdValue = destination.categoryId ?: 0L,
            onNavigateBack = onNavigateBack,
        )
    }
}
