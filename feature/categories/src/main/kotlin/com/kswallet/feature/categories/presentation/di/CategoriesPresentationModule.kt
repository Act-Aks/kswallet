package com.kswallet.feature.categories.presentation.di

import com.kswallet.feature.categories.presentation.editor.CategoryEditorViewModel
import com.kswallet.feature.categories.presentation.list.CategoriesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val categoriesPresentationModule = module {
    viewModel<CategoriesViewModel>()

    // Parameterised definition: the editor's id is passed at the call site with
    // `koinViewModel<CategoryEditorViewModel> { parametersOf(0L /* new */) }`.
    viewModel { parameters ->
        CategoryEditorViewModel(
            categoryIdValue = parameters.get(),
            observeCategory = get(),
            saveCategory = get(),
        )
    }
}
