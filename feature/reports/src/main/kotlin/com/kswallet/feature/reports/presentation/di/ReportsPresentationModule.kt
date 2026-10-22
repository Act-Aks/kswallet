package com.kswallet.feature.reports.presentation.di

import com.kswallet.feature.reports.presentation.reports.ReportsViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val reportsPresentationModule = module {
    viewModel<ReportsViewModel>()
}
