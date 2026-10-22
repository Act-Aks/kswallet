package com.kswallet.feature.budgets.presentation.di

import com.kswallet.feature.budgets.presentation.editor.BudgetEditorViewModel
import com.kswallet.feature.budgets.presentation.list.BudgetsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val budgetsPresentationModule = module {
    viewModel<BudgetsViewModel>()

    // Parameterised by the id of the budget being edited (0 = a new one).
    viewModel { parameters ->
        BudgetEditorViewModel(
            budgetIdValue = parameters.get(),
            observeBudget = get(),
            observeCategories = get(),
            observeBaseCurrency = get(),
            saveBudget = get(),
        )
    }
}
