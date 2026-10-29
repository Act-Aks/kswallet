package com.kswallet.feature.accounts.presentation.di

import com.kswallet.feature.accounts.presentation.AccountsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val accountsPresentationModule = module {

    viewModelOf(::AccountsViewModel)
}