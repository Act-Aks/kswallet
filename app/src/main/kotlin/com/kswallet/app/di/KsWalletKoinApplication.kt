package com.kswallet.app.di

import com.kswallet.core.database.di.DatabaseModule
import com.kswallet.core.datastore.di.SettingsModule
import com.kswallet.feature.accounts.data.di.AccountsDataModule
import com.kswallet.feature.accounts.domain.di.AccountsDomainModule
import com.kswallet.feature.accounts.presentation.di.AccountsPresentationModule
import com.kswallet.feature.budgets.data.di.BudgetsDataModule
import com.kswallet.feature.budgets.domain.di.BudgetsDomainModule
import com.kswallet.feature.budgets.presentation.di.BudgetsPresentationModule
import com.kswallet.feature.categories.data.di.CategoriesDataModule
import com.kswallet.feature.categories.domain.di.CategoriesDomainModule
import com.kswallet.feature.categories.presentation.di.CategoriesPresentationModule
import com.kswallet.feature.home.domain.di.HomeDomainModule
import com.kswallet.feature.home.presentation.di.HomePresentationModule
import com.kswallet.feature.reports.data.di.ReportsDataModule
import com.kswallet.feature.reports.domain.di.ReportsDomainModule
import com.kswallet.feature.reports.presentation.di.ReportsPresentationModule
import com.kswallet.feature.settings.domain.di.SettingsDomainModule
import com.kswallet.feature.settings.presentation.di.SettingsPresentationModule
import com.kswallet.feature.transactions.data.di.TransactionsDataModule
import com.kswallet.feature.transactions.domain.di.TransactionsDomainModule
import com.kswallet.feature.transactions.presentation.di.TransactionsPresentationModule
import org.koin.core.annotation.KoinApplication

/** Koin compiler-plugin entry point: all app modules are explicit and graph-checked. */
@KoinApplication(
    modules = [
        // Core
        AppModule::class,
        DatabaseModule::class,
        SettingsModule::class,

        // Features
        HomeDomainModule::class,
        HomePresentationModule::class,

        AccountsDataModule::class,
        AccountsDomainModule::class,
        AccountsPresentationModule::class,

        TransactionsDataModule::class,
        TransactionsDomainModule::class,
        TransactionsPresentationModule::class,

        CategoriesDataModule::class,
        CategoriesDomainModule::class,
        CategoriesPresentationModule::class,

        BudgetsDataModule::class,
        BudgetsDomainModule::class,
        BudgetsPresentationModule::class,
        
        ReportsDataModule::class,
        ReportsDomainModule::class,
        ReportsPresentationModule::class,

        SettingsDomainModule::class,
        SettingsPresentationModule::class,
    ],
)
class KsWalletKoinApplication
