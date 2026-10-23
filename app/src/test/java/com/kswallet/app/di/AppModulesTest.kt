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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.koin.core.annotation.KoinApplication

/** The annotated composition root is the source of truth for the app graph. */
class AppModulesTest {
    private val graphModules =
        KsWalletKoinApplication::class.java.getAnnotation(KoinApplication::class.java).modules.toSet()

    @Test
    fun `every core and feature module is explicitly part of the graph exactly once`() {
        val expected = setOf(
            AppModule::class,
            DatabaseModule::class,
            SettingsModule::class,
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
        )

        assertEquals(22, graphModules.size)
        assertEquals(expected, graphModules)
    }

    @Test
    fun `every feature layer module is included`() {
        val featureModules = graphModules.filter { it.java.packageName.startsWith("com.kswallet.feature.") }

        assertEquals(19, featureModules.size)
        assertTrue(
            featureModules.containsAll(
                setOf(
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
                    HomeDomainModule::class,
                    HomePresentationModule::class,
                    SettingsDomainModule::class,
                    SettingsPresentationModule::class,
                ),
            ),
        )
    }

    @Test
    fun `settings uses core datastore rather than a feature data module`() {
        assertTrue(graphModules.contains(SettingsModule::class))
        assertTrue(graphModules.none { it.java.packageName == "com.kswallet.feature.settings.data.di" })
    }
}
