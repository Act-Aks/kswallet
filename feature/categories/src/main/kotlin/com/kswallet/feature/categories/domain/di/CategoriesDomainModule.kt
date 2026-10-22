package com.kswallet.feature.categories.domain.di

import com.kswallet.feature.categories.domain.usecase.CountCategoriesUseCase
import com.kswallet.feature.categories.domain.usecase.DeleteCategoryUseCase
import com.kswallet.feature.categories.domain.usecase.GetCategoryUseCase
import com.kswallet.feature.categories.domain.usecase.ObserveCategoriesUseCase
import com.kswallet.feature.categories.domain.usecase.ObserveCategoryUseCase
import com.kswallet.feature.categories.domain.usecase.SaveCategoryUseCase
import com.kswallet.feature.categories.domain.usecase.SeedDefaultCategoriesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val categoriesDomainModule = module {
    factoryOf(::ObserveCategoriesUseCase)
    factoryOf(::ObserveCategoryUseCase)
    factoryOf(::GetCategoryUseCase)
    factoryOf(::SaveCategoryUseCase)
    factoryOf(::DeleteCategoryUseCase)
    factoryOf(::CountCategoriesUseCase)
    factoryOf(::SeedDefaultCategoriesUseCase)
}
