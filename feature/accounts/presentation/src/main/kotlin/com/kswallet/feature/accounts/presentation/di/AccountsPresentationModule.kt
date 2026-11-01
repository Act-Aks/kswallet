package com.kswallet.feature.accounts.presentation.di

import com.kswallet.feature.accounts.presentation.accounts.AccountsViewModel
import com.kswallet.feature.accounts.presentation.create_account.CreateAccountViewModel
import com.kswallet.feature.accounts.presentation.edit_account.EditAccountViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val accountsPresentationModule = module {
    viewModelOf(::AccountsViewModel)
    viewModelOf(::CreateAccountViewModel)
    viewModel { (accountId: AccountId) ->
        EditAccountViewModel(
            accountRepository = get(),
            accountId = accountId,
        )
    }
}