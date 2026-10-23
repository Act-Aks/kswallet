package com.kswallet.app.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.kswallet.app.R
import com.kswallet.core.designsystem.icon.KsWalletIcons
import com.kswallet.core.navigation.KsWalletDestination
import com.kswallet.core.navigation.TopLevelDestination
import com.kswallet.feature.accounts.presentation.navigation.AccountsDestination
import com.kswallet.feature.budgets.presentation.navigation.BudgetsDestination
import com.kswallet.feature.categories.presentation.navigation.CategoriesDestination
import com.kswallet.feature.home.presentation.navigation.HomeDestination
import com.kswallet.feature.reports.presentation.navigation.ReportsDestination
import com.kswallet.feature.transactions.presentation.navigation.TransactionsDestination

/**
 * The bottom bar, as data. Its routes and visual metadata are defined together so the tab
 * order, selected state, and presentation cannot drift apart.
 */
enum class KsWalletTopLevelDestination(
    override val route: KsWalletDestination,
    @StringRes override val labelRes: Int,
    val icon: ImageVector,
) : TopLevelDestination {
    HOME(HomeDestination, R.string.destination_home, KsWalletIcons.byKey("home")),
    TRANSACTIONS(TransactionsDestination, R.string.destination_transactions, KsWalletIcons.byKey("bills")),
    REPORTS(ReportsDestination, R.string.destination_reports, KsWalletIcons.reports),
    BUDGETS(BudgetsDestination, R.string.destination_budgets, KsWalletIcons.budgets),
    ACCOUNTS(AccountsDestination, R.string.destination_accounts, KsWalletIcons.wallet),
    CATEGORIES(CategoriesDestination, R.string.destination_categories, KsWalletIcons.byKey("category")),
}
