package com.kswallet.feature.accounts.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kswallet.core.designsystem.component.KsWalletLoadingState
import com.kswallet.core.designsystem.component.KsWalletScreenScaffold
import com.kswallet.core.designsystem.theme.KsWalletSpacing
import com.kswallet.core.model.Account
import com.kswallet.core.model.AccountId
import com.kswallet.core.model.AccountType
import com.kswallet.core.ui.util.MoneyFormatter
import com.kswallet.feature.accounts.R
import com.kswallet.feature.accounts.domain.usecase.ObserveAccountUseCase
import org.koin.compose.koinInject

/**
 * Read-only account detail.
 *
 * It lives in `:app` on purpose: it reads the accounts *domain* layer directly and owns no
 * state, so it does not need to be a feature yet. When it grows its own actions (archive,
 * re-order, per-account statistics) it moves into `:feature:accounts` unchanged.
 */
@Composable
fun AccountDetailRoute(
    accountIdValue: Long,
    onNavigateBack: () -> Unit,
    onEdit: () -> Unit,
    onOpenLedger: () -> Unit,
    modifier: Modifier = Modifier,
    observeAccount: ObserveAccountUseCase = koinInject(),
) {
    val accountId = AccountId(accountIdValue)
    val accountFlow = remember(accountId) { observeAccount(accountId) }
    val account by accountFlow.collectAsStateWithLifecycle(initialValue = null)

    AccountDetailScreen(
        account = account,
        onNavigateBack = onNavigateBack,
        onEdit = onEdit,
        onOpenLedger = onOpenLedger,
        modifier = modifier,
    )
}

@Composable
private fun AccountDetailScreen(
    account: Account?,
    onNavigateBack: () -> Unit,
    onEdit: () -> Unit,
    onOpenLedger: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KsWalletScreenScaffold(
        title = account?.name ?: stringResource(R.string.account_detail_missing),
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    ) { padding ->
        if (account == null) {
            KsWalletLoadingState(modifier = Modifier.padding(padding))
            return@KsWalletScreenScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = KsWalletSpacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.md),
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(KsWalletSpacing.md)) {
                    Text(
                        text = MoneyFormatter.format(account.balance),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = account.type.displayName(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            DetailRow(
                label = stringResource(R.string.account_detail_currency),
                value = "${account.currency.code} (${account.currency.symbol})",
            )
            DetailRow(
                label = stringResource(R.string.account_detail_opening_balance),
                value = MoneyFormatter.format(account.initialBalance),
            )
            DetailRow(
                label = stringResource(R.string.account_detail_included_in_total),
                value = stringResource(
                    if (account.includedInTotal) R.string.account_detail_yes else R.string.account_detail_no,
                ),
            )

            Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.account_detail_edit))
            }
            OutlinedButton(onClick = onOpenLedger, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.account_detail_open_ledger))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun AccountType.displayName(): String =
    name.lowercase().replaceFirstChar(Char::uppercase)
