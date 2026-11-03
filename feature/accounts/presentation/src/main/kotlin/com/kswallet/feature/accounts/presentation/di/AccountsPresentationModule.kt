package com.kswallet.feature.accounts.presentation.di

import com.kswallet.feature.accounts.presentation.accounts.AccountsViewModel
import com.kswallet.feature.accounts.presentation.create_account.CreateAccountViewModel
import com.kswallet.feature.accounts.presentation.edit_account.EditAccountViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val accountsPresentationModule = module {
    viewModel<AccountsViewModel>()
    viewModel<CreateAccountViewModel>()
    viewModel<EditAccountViewModel>()
}