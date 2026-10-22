package com.kswallet.feature.transactions.presentation.di

import com.kswallet.feature.transactions.presentation.editor.TransactionEditorViewModel
import com.kswallet.feature.transactions.presentation.list.TransactionsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val transactionsPresentationModule = module {
    viewModel<TransactionsViewModel>()

    // The editor is parameterized by the id of the entry being edited (0 = a new one).
    viewModel { parameters ->
        TransactionEditorViewModel(
            transactionIdValue = parameters.get(),
            observeAccounts = get(),
            observeCategories = get(),
            getTransaction = get(),
            saveTransaction = get(),
            timeProvider = get(),
        )
    }
}
