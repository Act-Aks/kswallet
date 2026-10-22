package com.kswallet.app.di

import com.kswallet.core.database.di.databaseModule
import com.kswallet.core.datastore.di.settingsModule
import com.kswallet.feature.accounts.data.di.accountsDataModule
import com.kswallet.feature.accounts.domain.di.accountsDomainModule
import com.kswallet.feature.accounts.presentation.di.accountsPresentationModule
import com.kswallet.feature.budgets.data.di.budgetsDataModule
import com.kswallet.feature.budgets.domain.di.budgetsDomainModule
import com.kswallet.feature.budgets.presentation.di.budgetsPresentationModule
import com.kswallet.feature.categories.data.di.categoriesDataModule
import com.kswallet.feature.categories.domain.di.categoriesDomainModule
import com.kswallet.feature.categories.presentation.di.categoriesPresentationModule
import com.kswallet.feature.home.domain.di.homeDomainModule
import com.kswallet.feature.home.presentation.di.homePresentationModule
import com.kswallet.feature.reports.data.di.reportsDataModule
import com.kswallet.feature.reports.domain.di.reportsDomainModule
import com.kswallet.feature.reports.presentation.di.reportsPresentationModule
import com.kswallet.feature.settings.domain.di.settingsDomainModule
import com.kswallet.feature.settings.presentation.di.settingsPresentationModule
import com.kswallet.feature.transactions.data.di.transactionsDataModule
import com.kswallet.feature.transactions.domain.di.transactionsDomainModule
import com.kswallet.feature.transactions.presentation.di.transactionsPresentationModule
import org.koin.core.module.Module

/**
 * Every Koin module of the app, in one place.
 *
 * Each feature contributes three layer modules (`data`, `domain`, `presentation`) and the
 * list is written out explicitly instead of being discovered by reflection: a feature that
 * is not listed here is simply not part of the graph.
 */
private val coreModules = listOf(
    appModule,
    databaseModule,
    settingsModule,
)

private val featureModules = listOf(
    homeDomainModule,
    homePresentationModule,

    accountsDataModule,
    accountsDomainModule,
    accountsPresentationModule,

    transactionsDataModule,
    transactionsDomainModule,
    transactionsPresentationModule,

    categoriesDataModule,
    categoriesDomainModule,
    categoriesPresentationModule,

    budgetsDataModule,
    budgetsDomainModule,
    budgetsPresentationModule,

    reportsDataModule,
    reportsDomainModule,
    reportsPresentationModule,

    settingsDomainModule,
    settingsPresentationModule,
)

val appModules: List<Module> = coreModules + featureModules
