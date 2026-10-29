package com.kswallet.feature.accounts.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neodesign.component.NeoButton
import com.neodesign.component.NeoButtonVariant
import com.neodesign.component.NeoCard
import com.neodesign.component.NeoText
import com.neodesign.theme.NeoTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun AccountsRoot(
    viewModel: AccountsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AccountsScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun AccountsScreen(
    state: AccountsState,
    onAction: (AccountsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeoTheme.colors.background)
            .safeDrawingPadding()
            .padding(NeoTheme.spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(
            NeoTheme.spacing.extraLarge,
        ),
    ) {
        NeoText(
            text = stringResource(R.string.accounts_title),
            style = NeoTheme.typography.display,
        )

        when (val content = state.content) {
            AccountsContent.Loading -> LoadingContent()

            AccountsContent.Empty -> EmptyContent()

            is AccountsContent.Content -> AccountsList(
                accounts = content.accounts,
            )

            AccountsContent.Error -> ErrorContent(
                onRetry = {
                    onAction(AccountsAction.RetryClicked)
                },
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        NeoText(
            text = stringResource(R.string.accounts_loading),
            style = NeoTheme.typography.headline,
        )
    }
}

@Composable
private fun EmptyContent() {
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        NeoText(
            text = stringResource(R.string.accounts_empty_title),
            style = NeoTheme.typography.headline,
        )

        NeoText(
            text = stringResource(R.string.accounts_empty_description),
            color = NeoTheme.colors.contentSecondary,
        )
    }
}

@Composable
private fun ErrorContent(
    onRetry: () -> Unit,
) {
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        NeoText(
            text = stringResource(R.string.accounts_error_title),
            style = NeoTheme.typography.headline,
        )

        NeoText(
            text = stringResource(R.string.accounts_error_description),
            color = NeoTheme.colors.contentSecondary,
        )

        NeoButton(
            text = stringResource(R.string.accounts_retry),
            onClick = onRetry,
            variant = NeoButtonVariant.Outlined,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AccountsList(
    accounts: List<AccountListItem>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            NeoTheme.spacing.medium,
        ),
    ) {
        items(
            items = accounts,
            key = { account -> account.id },
        ) { account ->
            AccountCard(account = account)
        }
    }
}

@Composable
private fun AccountCard(
    account: AccountListItem,
) {
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                NeoTheme.spacing.extraSmall,
            ),
        ) {
            NeoText(
                text = account.name,
                style = NeoTheme.typography.title,
            )

            NeoText(
                text = account.currencyCode,
                color = NeoTheme.colors.contentSecondary,
            )

            NeoText(
                text = if (account.isArchived) {
                    stringResource(R.string.accounts_status_archived)
                } else {
                    stringResource(R.string.accounts_status_active)
                },
                color = NeoTheme.colors.contentSecondary,
            )
        }
    }
}