package com.kswallet.feature.categories.domain.di

import com.kswallet.core.common.time.TimeProvider
import com.kswallet.feature.categories.domain.repository.CategoryRepository
import com.kswallet.feature.categories.domain.usecase.CountCategoriesUseCase
import com.kswallet.feature.categories.domain.usecase.DeleteCategoryUseCase
import com.kswallet.feature.categories.domain.usecase.GetCategoryUseCase
import com.kswallet.feature.categories.domain.usecase.ObserveCategoriesUseCase
import com.kswallet.feature.categories.domain.usecase.ObserveCategoryUseCase
import com.kswallet.feature.categories.domain.usecase.SaveCategoryUseCase
import com.kswallet.feature.categories.domain.usecase.SeedDefaultCategoriesUseCase
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module

@Module
class CategoriesDomainModule {
    @Factory
    fun provideObserveCategoriesUseCase(
        repository: CategoryRepository,
    ): ObserveCategoriesUseCase =
        ObserveCategoriesUseCase(repository)

    @Factory
    fun provideObserveCategoryUseCase(
        repository: CategoryRepository,
    ): ObserveCategoryUseCase =
        ObserveCategoryUseCase(repository)

    @Factory
    fun provideGetCategoryUseCase(
        repository: CategoryRepository,
    ): GetCategoryUseCase =
        GetCategoryUseCase(repository)

    @Factory
    fun provideSaveCategoryUseCase(
        repository: CategoryRepository,
        timeProvider: TimeProvider,
    ): SaveCategoryUseCase =
        SaveCategoryUseCase(repository, timeProvider)

    @Factory
    fun provideDeleteCategoryUseCase(
        repository: CategoryRepository,
    ): DeleteCategoryUseCase =
        DeleteCategoryUseCase(repository)

    @Factory
    fun provideCountCategoriesUseCase(
        repository: CategoryRepository,
    ): CountCategoriesUseCase =
        CountCategoriesUseCase(repository)

    @Factory
    fun provideSeedDefaultCategoriesUseCase(
        repository: CategoryRepository,
        timeProvider: TimeProvider,
    ): SeedDefaultCategoriesUseCase =
        SeedDefaultCategoriesUseCase(repository, timeProvider)
}
