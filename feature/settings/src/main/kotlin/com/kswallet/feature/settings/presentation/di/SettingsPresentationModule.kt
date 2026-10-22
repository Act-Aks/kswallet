package com.kswallet.feature.settings.presentation.di

import com.kswallet.feature.settings.presentation.settings.SettingsViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val settingsPresentationModule = module {
    viewModel<SettingsViewModel>()
}
