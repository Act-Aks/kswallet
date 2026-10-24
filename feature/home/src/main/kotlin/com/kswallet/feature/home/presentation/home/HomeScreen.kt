package com.kswallet.feature.home.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kswallet.core.designsystem.component.KsWalletEmptyState
import com.kswallet.core.designsystem.component.KsWalletLoadingState
import com.kswallet.core.designsystem.component.KsWalletScreenScaffold
import com.kswallet.core.designsystem.component.KsWalletSectionHeader
import com.kswallet.core.designsystem.icon.KsWalletIcons
import com.kswallet.core.designsystem.theme.KsWalletSpacing
import com.kswallet.core.model.Money
import com.kswallet.core.ui.components.KsWalletAmountText
import com.kswallet.core.ui.components.LaunchMessage
import com.kswallet.core.ui.screen.ObserveAsEvents
import com.kswallet.core.ui.util.MoneyFormatter
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.core.ui.util.onColorFor
import com.kswallet.core.ui.util.toComposeColor
import com.kswallet.feature.home.R
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoot(
    onAddTransaction: () -> Unit,
    onOpenTransaction: (Long) -> Unit,
    onAddAccount: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var message by remember { mutableStateOf<UiMessage?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is HomeEvent.OpenTransactionEditor -> {
                event.transactionId?.let(onOpenTransaction) ?: onAddTransaction()
            }

            HomeEvent.OpenAccountEditor -> onAddAccount()
            HomeEvent.NavigateToAccounts -> onNavigateToAccounts()
            HomeEvent.NavigateToTransactions -> onNavigateToTransactions()
            is HomeEvent.ShowMessage -> message = event.message
        }
    }

    snackbarHostState.LaunchMessage(message) { message = null }

    HomeScreen(
        state = state,
        onAction = viewModel::onAction,
        onOpenSettings = onOpenSettings,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
internal fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
    onOpenSettings: (() -> Unit)? = null,
    snackbarHostState: SnackbarHostState? = null,
) {
    KsWalletScreenScaffold(
        title = stringResource(R.string.home_title),
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        actions = {
            if (onOpenSettings != null) {
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = KsWalletIcons.settings,
                        contentDescription = stringResource(R.string.home_open_settings),
                    )
                }
            }
        },
    ) { padding ->
        when {
            state.isLoading -> KsWalletLoadingState(modifier = Modifier.padding(padding))

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = KsWalletSpacing.xl),
                verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
            ) {
                item(key = "balance") {
                    BalanceCard(
                        state = state,
                        modifier = Modifier.padding(horizontal = KsWalletSpacing.screenHorizontal),
                    )
                }

                item(key = "actions") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = KsWalletSpacing.screenHorizontal),
                        horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
                    ) {
                        Button(
                            onClick = { onAction(HomeAction.AddTransactionClicked) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(KsWalletIcons.byKey("income"), contentDescription = null)
                            Text(
                                text = stringResource(R.string.home_add_transaction),
                                modifier = Modifier.padding(start = KsWalletSpacing.sm),
                            )
                        }
                        OutlinedButton(
                            onClick = { onAction(HomeAction.SeeAllTransactionsClicked) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringResource(R.string.home_see_all))
                        }
                    }
                }

                if (state.needsFirstAccount) {
                    item(key = "first-run") {
                        KsWalletEmptyState(
                            title = stringResource(R.string.home_no_accounts_title),
                            message = stringResource(R.string.home_no_accounts_message),
                            icon = KsWalletIcons.bank,
                            actionLabel = stringResource(R.string.home_add_account),
                            onAction = { onAction(HomeAction.AddAccountClicked) },
                        )
                    }
                }

                item(key = "recent-header") {
                    KsWalletSectionHeader(text = stringResource(R.string.home_recent))
                }

                if (state.recentTransactions.isEmpty()) {
                    item(key = "recent-empty") {
                        Text(
                            text = stringResource(R.string.home_recent_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = KsWalletSpacing.screenHorizontal),
                        )
                    }
                }

                items(items = state.recentTransactions, key = { it.id }) { transaction ->
                    RecentTransactionRow(
                        transaction = transaction,
                        onClick = { onAction(HomeAction.TransactionClicked(transaction.id)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(state: HomeState, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KsWalletSpacing.md),
            verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.xs),
        ) {
            Text(
                text = stringResource(R.string.home_total_balance),
                style = MaterialTheme.typography.labelLarge,
            )
            state.totalBalance?.let { balance ->
                Text(
                    text = MoneyFormatter.format(balance),
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.md)) {
                state.monthIncome?.let { income ->
                    Column {
                        Text(stringResource(R.string.home_income), style = MaterialTheme.typography.labelSmall)
                        Text(MoneyFormatter.format(income), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                state.monthExpense?.let { expense ->
                    Column {
                        Text(stringResource(R.string.home_expense), style = MaterialTheme.typography.labelSmall)
                        Text(MoneyFormatter.format(expense), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                state.netThisMonth?.let { net ->
                    Column {
                        Text(stringResource(R.string.home_net), style = MaterialTheme.typography.labelSmall)
                        NetText(net)
                    }
                }
            }
        }
    }
}

@Composable
private fun NetText(net: Money) {
    Text(
        text = MoneyFormatter.formatSigned(net),
        style = MaterialTheme.typography.bodyMedium,
        color = if (net.isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun RecentTransactionRow(transaction: RecentTransactionUi, onClick: () -> Unit) {
    val accent = transaction.colorArgb?.toComposeColor() ?: MaterialTheme.colorScheme.secondaryContainer

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = KsWalletSpacing.screenHorizontal, vertical = KsWalletSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(color = accent, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = KsWalletIcons.byKey(transaction.iconKey ?: KsWalletIcons.fallbackKey),
                contentDescription = null,
                tint = accent.onColorFor(),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = transaction.title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = transaction.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
        KsWalletAmountText(money = transaction.amount, type = transaction.type)
    }
}
