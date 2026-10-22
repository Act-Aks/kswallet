package com.kswallet.feature.budgets.presentation.di

import com.kswallet.feature.budgets.presentation.editor.BudgetEditorViewModel
import com.kswallet.feature.budgets.presentation.list.BudgetsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val budgetsPresentationModule = module {
    viewModelOf(::BudgetsViewModel)

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
