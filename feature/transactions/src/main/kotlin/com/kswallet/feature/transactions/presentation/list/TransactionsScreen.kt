package com.kswallet.feature.transactions.presentation.list

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.kswallet.core.designsystem.component.KsWalletConfirmDialog
import com.kswallet.core.designsystem.component.KsWalletEmptyState
import com.kswallet.core.designsystem.component.KsWalletLoadingState
import com.kswallet.core.designsystem.component.KsWalletScreenScaffold
import com.kswallet.core.designsystem.icon.KsWalletIcons
import com.kswallet.core.designsystem.theme.KsWalletColors
import com.kswallet.core.designsystem.theme.KsWalletSpacing
import com.kswallet.core.model.Money
import com.kswallet.core.model.TransactionId
import com.kswallet.core.model.TransactionType
import com.kswallet.core.ui.components.KsWalletAmountText
import com.kswallet.core.ui.components.LaunchMessage
import com.kswallet.core.ui.screen.ObserveAsEvents
import com.kswallet.core.ui.util.MoneyFormatter
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.core.ui.util.onColorFor
import com.kswallet.core.ui.util.toComposeColor
import com.kswallet.feature.transactions.R
import com.kswallet.feature.transactions.domain.model.PeriodOption
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TransactionsRoute(
    onOpenEditor: (TransactionId?) -> Unit,
    viewModel: TransactionsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var message by remember { mutableStateOf<UiMessage?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is TransactionsEvent.OpenEditor -> onOpenEditor(event.transactionId)
            is TransactionsEvent.ShowMessage -> message = event.message
        }
    }

    snackbarHostState.LaunchMessage(message) { message = null }

    TransactionsScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
internal fun TransactionsScreen(
    state: TransactionsState,
    onAction: (TransactionsAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
) {
    KsWalletScreenScaffold(
        title = stringResource(R.string.transactions_title),
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(TransactionsAction.AddTransactionClicked) }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.transactions_add))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            PeriodSummary(
                state = state,
                onPeriodSelected = { onAction(TransactionsAction.PeriodSelected(it)) },
                onTypeSelected = { onAction(TransactionsAction.TypeFilterSelected(it)) },
            )

            when {
                state.isLoading -> KsWalletLoadingState(modifier = Modifier.fillMaxSize())

                state.isEmpty -> KsWalletEmptyState(
                    title = stringResource(R.string.transactions_empty_title),
                    message = stringResource(R.string.transactions_empty_message),
                    icon = KsWalletIcons.other,
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = KsWalletSpacing.xl),
                ) {
                    state.dayGroups.forEach { group ->
                        item(key = "header-${group.header}") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = KsWalletSpacing.screenHorizontal,
                                        vertical = KsWalletSpacing.sm,
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = group.header,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = MoneyFormatter.formatSigned(group.dayTotal),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = group.dayTotal.signColor(),
                                )
                            }
                        }

                        items(items = group.items, key = { it.id.value }) { item ->
                            TransactionRow(
                                item = item,
                                onDelete = { onAction(TransactionsAction.DeleteClicked(item.id)) },
                                onOpen = { onAction(TransactionsAction.TransactionClicked(item.id)) },
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        }
                    }
                }
            }
        }
    }

    state.pendingDelete?.let { pending ->
        KsWalletConfirmDialog(
            title = stringResource(R.string.transaction_delete_title),
            message = stringResource(R.string.transaction_delete_message, pending.title),
            confirmLabel = stringResource(R.string.action_delete),
            onConfirm = { onAction(TransactionsAction.DeleteConfirmed) },
            onDismiss = { onAction(TransactionsAction.DeleteDismissed) },
            destructive = true,
        )
    }
}

@Composable
private fun PeriodSummary(
    state: TransactionsState,
    onPeriodSelected: (PeriodOption) -> Unit,
    onTypeSelected: (TransactionType?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KsWalletSpacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(KsWalletSpacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.transactions_income),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    state.periodIncome?.let {
                        Text(
                            MoneyFormatter.format(it),
                            style = MaterialTheme.typography.titleMedium,
                            color = KsWalletColors.Income,
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.transactions_expense),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    state.periodExpense?.let {
                        Text(
                            MoneyFormatter.format(it),
                            style = MaterialTheme.typography.titleMedium,
                            color = KsWalletColors.Expense,
                        )
                    }
                }
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.xs)) {
            items(items = PeriodOption.entries, key = { it.name }) { period ->
                FilterChip(
                    selected = state.filter.period == period,
                    onClick = { onPeriodSelected(period) },
                    label = { Text(period.label()) },
                )
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.xs)) {
            item(key = "all-types") {
                FilterChip(
                    selected = state.filter.type == null,
                    onClick = { onTypeSelected(null) },
                    label = { Text(stringResource(R.string.transactions_filter_all)) },
                )
            }
            items(items = TransactionType.entries, key = { it.name }) { type ->
                FilterChip(
                    selected = state.filter.type == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(type.label()) },
                )
            }
        }
    }
}

@Composable
private fun TransactionRow(
    item: TransactionItemUi,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
) {
    val accent = item.colorArgb?.toComposeColor() ?: MaterialTheme.colorScheme.secondaryContainer

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .padding(horizontal = KsWalletSpacing.screenHorizontal, vertical = KsWalletSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color = accent, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = KsWalletIcons.byKey(item.iconKey ?: KsWalletIcons.fallbackKey),
                contentDescription = null,
                tint = accent.onColorFor(),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }

        KsWalletAmountText(money = item.amount, type = item.type)

        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
        }
    }
}

@Composable
private fun Money.signColor() = when {
    isZero -> MaterialTheme.colorScheme.onSurfaceVariant
    isNegative -> KsWalletColors.Expense
    else -> KsWalletColors.Income
}

@Composable
private fun TransactionType.label() = stringResource(
    when (this) {
        TransactionType.INCOME -> R.string.transaction_type_income
        TransactionType.EXPENSE -> R.string.transaction_type_expense
        TransactionType.TRANSFER -> R.string.transaction_type_transfer
    },
)

@Composable
private fun PeriodOption.label() = stringResource(
    when (this) {
        PeriodOption.THIS_MONTH -> R.string.period_this_month
        PeriodOption.LAST_30_DAYS -> R.string.period_last_30_days
        PeriodOption.THIS_YEAR -> R.string.period_this_year
        PeriodOption.ALL_TIME -> R.string.period_all_time
    },
)
