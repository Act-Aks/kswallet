package com.kswallet.feature.accounts.presentation.list

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kswallet.core.designsystem.component.KsWalletConfirmDialog
import com.kswallet.core.designsystem.component.KsWalletEmptyState
import com.kswallet.core.designsystem.component.KsWalletLoadingState
import com.kswallet.core.designsystem.component.KsWalletScreenScaffold
import com.kswallet.core.designsystem.icon.KsWalletIcons
import com.kswallet.core.designsystem.theme.KsWalletSpacing
import com.kswallet.core.designsystem.theme.MoneyHeaderTextStyle
import com.kswallet.core.model.AccountId
import com.kswallet.core.ui.components.LaunchMessage
import com.kswallet.core.ui.screen.ObserveAsEvents
import com.kswallet.core.ui.util.MoneyFormatter
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.core.ui.util.onColorFor
import com.kswallet.core.ui.util.toComposeColor
import com.kswallet.feature.accounts.R
import com.kswallet.feature.accounts.presentation.util.icon
import com.kswallet.feature.accounts.presentation.util.label
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccountsRoute(
    onAddAccount: () -> Unit,
    onOpenAccount: (AccountId) -> Unit,
    viewModel: AccountsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var message by remember { mutableStateOf<UiMessage?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is AccountsEvent.OpenEditor -> onAddAccount()
            is AccountsEvent.OpenAccountDetail -> onOpenAccount(event.id)
            is AccountsEvent.ShowMessage -> message = event.message
        }
    }

    snackbarHostState.LaunchMessage(message) { message = null }

    AccountsScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
internal fun AccountsScreen(
    state: AccountsState,
    onAction: (AccountsAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
) {
    KsWalletScreenScaffold(
        title = stringResource(R.string.accounts_title),
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(AccountsAction.AddAccountClicked) }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.accounts_add))
            }
        },
    ) { padding ->
        when {
            state.isLoading -> KsWalletLoadingState(modifier = Modifier.padding(padding))

            state.isEmpty -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                KsWalletEmptyState(
                    title = stringResource(R.string.accounts_empty_title),
                    message = stringResource(R.string.accounts_empty_message),
                    icon = KsWalletIcons.bank,
                )
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(KsWalletSpacing.screenHorizontal, KsWalletSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
            ) {
                item(key = "total") {
                    state.totalBalance?.let { total ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(KsWalletSpacing.md)) {
                                Text(
                                    text = stringResource(R.string.accounts_total_balance),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = MoneyFormatter.format(total),
                                    style = MoneyHeaderTextStyle,
                                )
                                if (state.hiddenForeignCurrencyAccounts > 0) {
                                    Text(
                                        text = pluralStringResource(
                                            R.plurals.accounts_foreign_currency_note,
                                            state.hiddenForeignCurrencyAccounts,
                                            state.hiddenForeignCurrencyAccounts,
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }

                items(items = state.accounts, key = { it.id.value }) { account ->
                    AccountCard(
                        account = account,
                        onClick = { onAction(AccountsAction.AccountClicked(account.id)) },
                        onDelete = { onAction(AccountsAction.DeleteClicked(account.id)) },
                    )
                }
            }
        }
    }

    state.pendingDelete?.let { pending ->
        KsWalletConfirmDialog(
            title = stringResource(R.string.account_delete_title, pending.name),
            message = if (pending.transactionCount == 0) {
                stringResource(R.string.account_delete_message)
            } else {
                pluralStringResource(
                    R.plurals.account_delete_message_with_transactions,
                    pending.transactionCount,
                    pending.transactionCount,
                )
            },
            confirmLabel = stringResource(R.string.action_delete),
            onConfirm = { onAction(AccountsAction.DeleteConfirmed) },
            onDismiss = { onAction(AccountsAction.DeleteDismissed) },
            destructive = true,
        )
    }
}

@Composable
private fun AccountCard(
    account: AccountCardUi,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val color = account.colorArgb.toComposeColor()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KsWalletSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color = color, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = account.type.icon(),
                    contentDescription = account.type.name,
                    tint = color.onColorFor(),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = account.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = stringResource(
                        if (account.includedInTotal) {
                            R.string.account_subtitle_included
                        } else {
                            R.string.account_subtitle_excluded
                        },
                        account.type.label(),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = MoneyFormatter.format(account.balance),
                style = MaterialTheme.typography.titleMedium,
            )

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
            }
        }
    }
}
