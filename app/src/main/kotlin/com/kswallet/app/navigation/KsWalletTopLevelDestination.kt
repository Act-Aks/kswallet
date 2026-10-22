package com.kswallet.app.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.kswallet.app.R
import com.kswallet.core.designsystem.icon.KsWalletIcons
import com.kswallet.core.navigation.KsWalletDestination
import com.kswallet.core.navigation.TopLevelDestination
import com.kswallet.core.navigation.navigateTo
import com.kswallet.feature.accounts.presentation.navigation.AccountsDestination
import com.kswallet.feature.budgets.presentation.navigation.BudgetsDestination
import com.kswallet.feature.categories.presentation.navigation.CategoriesDestination
import com.kswallet.feature.home.presentation.navigation.HomeDestination
import com.kswallet.feature.reports.presentation.navigation.ReportsDestination
import com.kswallet.feature.transactions.presentation.navigation.TransactionsDestination

/**
 * The bottom bar, as data.
 *
 * Adding a destination is one enum entry: the bar, the "is this tab selected" logic and the
 * back-stack behavior all read from this list, so they cannot drift apart.
 *
 * Six entries is the ceiling for a bottom bar on a phone; a seventh feature belongs behind a
 * "more" destination rather than in this enum, or the labels stop fitting next to their icons.
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

/**
 * Switches tabs the way a bottom bar should: the state of the tab you leave is saved, the
 * tab is never stacked twice, and the back stack of the whole graph does not grow.
 */
internal fun NavController.navigateTopLevelTo(destination: TopLevelDestination) {
    navigateTo(destination.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
