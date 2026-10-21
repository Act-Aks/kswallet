package com.kswallet.feature.accounts.presentation.di

import com.kswallet.feature.accounts.presentation.editor.AccountEditorViewModel
import com.kswallet.feature.accounts.presentation.list.AccountsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val accountsPresentationModule = module {
    viewModel<AccountsViewModel>()

    viewModel { parameters ->
        AccountEditorViewModel(
            accountIdValue = parameters.get(),
            getAccount = get(),
            saveAccount = get(),
            settingsRepository = get(),
        )
    }
}
