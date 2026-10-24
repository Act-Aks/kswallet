package com.kswallet.app.navigation

import androidx.navigation3.runtime.NavKey
import com.kswallet.app.R
import com.kswallet.core.designsystem.component.KsWalletTab
import com.kswallet.core.designsystem.icon.KsWalletIcons
import com.kswallet.feature.accounts.presentation.navigation.AccountsRoute
import com.kswallet.feature.budgets.presentation.navigation.BudgetsRoute
import com.kswallet.feature.categories.presentation.navigation.CategoriesRoute
import com.kswallet.feature.home.presentation.navigation.HomeRoute
import com.kswallet.feature.reports.presentation.navigation.ReportsRoute
import com.kswallet.feature.transactions.presentation.navigation.TransactionsRoute

val ksWalletTabs = listOf(
    KsWalletTab(
        route = HomeRoute,
        label = R.string.destination_home,
        selectedIcon = KsWalletIcons.byKey("home"),
        unselectedIcon = KsWalletIcons.byKey("home"),
    ),
    KsWalletTab(
        route = TransactionsRoute,
        label = R.string.destination_transactions,
        selectedIcon = KsWalletIcons.byKey("bills"),
        unselectedIcon = KsWalletIcons.byKey("bills"),
    ),
    KsWalletTab(
        route = ReportsRoute,
        label = R.string.destination_reports,
        selectedIcon = KsWalletIcons.reports,
        unselectedIcon = KsWalletIcons.reports,
    ),
    KsWalletTab(
        route = BudgetsRoute,
        label = R.string.destination_budgets,
        selectedIcon = KsWalletIcons.budgets,
        unselectedIcon = KsWalletIcons.budgets,
    ),
    KsWalletTab(
        route = AccountsRoute,
        label = R.string.destination_accounts,
        selectedIcon = KsWalletIcons.wallet,
        unselectedIcon = KsWalletIcons.wallet,
    ),
    KsWalletTab(
        route = CategoriesRoute,
        label = R.string.destination_categories,
        selectedIcon = KsWalletIcons.byKey("category"),
        unselectedIcon = KsWalletIcons.byKey("category"),
    ),
)

val topLevelRoutes: List<NavKey> = ksWalletTabs.map { it.route }
