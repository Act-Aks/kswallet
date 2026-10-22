package com.kswallet.feature.categories.data.di

import com.kswallet.feature.categories.data.repository.OfflineFirstCategoryRepository
import com.kswallet.feature.categories.domain.repository.CategoryRepository
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val categoriesDataModule = module {
    single<OfflineFirstCategoryRepository>() bind CategoryRepository::class
}
