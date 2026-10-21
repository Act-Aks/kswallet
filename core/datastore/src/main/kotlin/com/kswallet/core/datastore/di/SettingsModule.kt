package com.kswallet.core.datastore.di

import com.kswallet.core.datastore.data.OfflineFirstSettingsRepository
import com.kswallet.core.datastore.domain.repository.SettingsRepository
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val settingsModule = module {
    single<OfflineFirstSettingsRepository>() bind SettingsRepository::class
}
