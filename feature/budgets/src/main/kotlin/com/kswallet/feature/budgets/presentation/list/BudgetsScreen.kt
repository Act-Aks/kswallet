package com.kswallet.feature.budgets.presentation.list

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.kswallet.core.designsystem.theme.KsWalletSpacing
import com.kswallet.core.model.BudgetId
import com.kswallet.core.ui.components.LaunchMessage
import com.kswallet.core.ui.screen.ObserveAsEvents
import com.kswallet.core.ui.util.MoneyFormatter
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.core.ui.util.onColorFor
import com.kswallet.core.ui.util.toComposeColor
import com.kswallet.feature.budgets.R
import org.koin.compose.viewmodel.koinViewModel

/**
 * Budgets.
 *
 * Each row answers the only question a budget has: how much of the limit is gone. The bar
 * turns into the error color when the limit is passed and the numbers underneath spell the
 * same thing out, so color is never the only signal.
 */
@Composable
fun BudgetsRoute(
    onAddBudget: () -> Unit,
    onEditBudget: (BudgetId) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BudgetsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var message by remember { mutableStateOf<UiMessage?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is BudgetsEvent.OpenEditor -> {
                event.budgetId?.let(onEditBudget) ?: onAddBudget()
            }

            is BudgetsEvent.ShowMessage -> message = event.message
        }
    }

    snackbarHostState.LaunchMessage(message) { message = null }

    BudgetsScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
internal fun BudgetsScreen(
    state: BudgetsState,
    onAction: (BudgetsAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
) {
    KsWalletScreenScaffold(
        title = stringResource(R.string.budgets_title),
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        floatingActionButton = {
            if (!state.isEmpty) {
                ExtendedFloatingActionButton(
                    onClick = { onAction(BudgetsAction.AddBudgetClicked) },
                    icon = { Icon(imageVector = KsWalletIcons.budgets, contentDescription = null) },
                    text = { Text(text = stringResource(R.string.budgets_new)) },
                )
            }
        },
    ) { padding ->
        when {
            state.isLoading -> KsWalletLoadingState(modifier = Modifier.padding(padding))

            state.isEmpty -> KsWalletEmptyState(
                title = stringResource(R.string.budgets_empty_title),
                message = stringResource(R.string.budgets_empty_message),
                icon = KsWalletIcons.budgets,
                actionLabel = stringResource(R.string.budgets_empty_action),
                onAction = { onAction(BudgetsAction.AddBudgetClicked) },
                modifier = Modifier.padding(padding),
            )

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    start = KsWalletSpacing.screenHorizontal,
                    end = KsWalletSpacing.screenHorizontal,
                    bottom = KsWalletSpacing.xl,
                ),
                verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
            ) {
                items(items = state.budgets, key = { it.id.value }) { row ->
                    BudgetCard(
                        row = row,
                        onClick = { onAction(BudgetsAction.BudgetClicked(row.id)) },
                        onDelete = { onAction(BudgetsAction.DeleteClicked(row.id)) },
                    )
                }
            }
        }
    }

    state.pendingDelete?.let { pending ->
        KsWalletConfirmDialog(
            title = stringResource(R.string.budget_delete_title, pending.categoryName),
            message = stringResource(R.string.budget_delete_message),
            confirmLabel = stringResource(R.string.action_delete),
            onConfirm = { onAction(BudgetsAction.DeleteConfirmed) },
            onDismiss = { onAction(BudgetsAction.DeleteDismissed) },
            destructive = true,
        )
    }
}

@Composable
private fun BudgetCard(
    row: BudgetRowUi,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KsWalletSpacing.md),
            verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color = row.colorArgb.toComposeColor(), shape = CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = KsWalletIcons.byKey(row.iconKey),
                        contentDescription = null,
                        tint = row.colorArgb.toComposeColor().onColorFor(),
                        modifier = Modifier.size(20.dp),
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = row.categoryName, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = when (row.periodLabel) {
                            BudgetPeriodLabel.WEEKLY ->
                                stringResource(R.string.budget_spent_this_week, MoneyFormatter.format(row.spent))

                            BudgetPeriodLabel.MONTHLY ->
                                stringResource(R.string.budget_spent_this_month, MoneyFormatter.format(row.spent))

                            BudgetPeriodLabel.YEARLY ->
                                stringResource(R.string.budget_spent_this_year, MoneyFormatter.format(row.spent))
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.action_delete),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            LinearProgressIndicator(
                progress = { row.fractionUsed },
                modifier = Modifier.fillMaxWidth(),
                color = if (row.isOverBudget) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(
                        R.string.budget_spent_of_limit,
                        MoneyFormatter.format(row.spent),
                        MoneyFormatter.format(row.limit),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = if (row.isOverBudget) {
                        stringResource(R.string.budget_over_by, MoneyFormatter.format(row.spent - row.limit))
                    } else {
                        stringResource(R.string.budget_left, MoneyFormatter.format(row.limit - row.spent))
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (row.isOverBudget) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}
