package com.kswallet.feature.home.presentation.di

import com.kswallet.feature.home.presentation.home.HomeViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val homePresentationModule = module {
    viewModel<HomeViewModel>()
}
