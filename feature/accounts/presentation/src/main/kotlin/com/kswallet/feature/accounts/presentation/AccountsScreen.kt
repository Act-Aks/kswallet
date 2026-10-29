package com.kswallet.feature.accounts.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kswallet.core.money.CurrencyRegistry
import com.kswallet.core.money.CurrencyUnit
import com.neodesign.component.NeoButton
import com.neodesign.component.NeoButtonVariant
import com.neodesign.component.NeoCard
import com.neodesign.component.NeoSegmentOption
import com.neodesign.component.NeoSegmentedControl
import com.neodesign.component.NeoText
import com.neodesign.component.NeoTextField
import com.neodesign.theme.NeoTheme
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccountsRoot(
    viewModel: AccountsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currencyRegistry = koinInject<CurrencyRegistry>()

    NeoTheme {
        AccountsScreen(
            state = state,
            currencies = currencyRegistry.supportedCurrencies(),
            onAction = viewModel::onAction,
        )
    }
}

@Composable
private fun AccountsScreen(
    state: AccountsState,
    currencies: List<CurrencyUnit>,
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

        CreateAccountCard(
            state = state.createAccount,
            currencies = currencies,
            onAction = onAction,
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
        Column(
            verticalArrangement = Arrangement.spacedBy(
                NeoTheme.spacing.small,
            ),
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
}

@Composable
private fun ErrorContent(
    onRetry: () -> Unit,
) {
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                NeoTheme.spacing.medium,
            ),
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
}

@Composable
private fun CreateAccountCard(
    state: CreateAccountState,
    currencies: List<CurrencyUnit>,
    onAction: (AccountsAction) -> Unit,
) {
    if (!state.isVisible) {
        NeoButton(
            text = stringResource(R.string.accounts_create),
            onClick = {
                onAction(AccountsAction.ShowCreateAccountClicked)
            },
            modifier = Modifier.fillMaxWidth(),
        )
        return
    }

    NeoCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                NeoTheme.spacing.medium,
            ),
        ) {
            NeoText(
                text = stringResource(R.string.accounts_create_title),
                style = NeoTheme.typography.title,
            )

            NameField(
                state = state,
                onAction = onAction,
            )

            CurrencySelector(
                state = state,
                currencies = currencies,
                onAction = onAction,
            )

            if (state.submissionError != null) {
                NeoText(
                    text = state.submissionError,
                    color = NeoTheme.colors.negative,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    NeoTheme.spacing.medium,
                ),
            ) {
                NeoButton(
                    text = stringResource(R.string.accounts_create_cancel),
                    onClick = {
                        onAction(AccountsAction.DismissCreateAccountClicked)
                    },
                    variant = NeoButtonVariant.Outlined,
                    modifier = Modifier.weight(1f),
                )

                NeoButton(
                    text = stringResource(R.string.accounts_create_submit),
                    onClick = {
                        onAction(AccountsAction.SubmitCreateAccountClicked)
                    },
                    enabled = state.canSubmit,
                    loading = state.isSubmitting,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun NameField(
    state: CreateAccountState,
    onAction: (AccountsAction) -> Unit,
) {
    val nameState = rememberTextFieldState(
        initialText = state.name,
    )

    LaunchedEffect(nameState) {
        snapshotFlow { nameState.text.toString() }
            .collect { value ->
                onAction(AccountsAction.NameChanged(value))
            }
    }

    NeoTextField(
        state = nameState,
        label = stringResource(R.string.accounts_create_name_label),
        errorMessage = state.nameError,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun CurrencySelector(
    state: CreateAccountState,
    currencies: List<CurrencyUnit>,
    onAction: (AccountsAction) -> Unit,
) {
    NeoSegmentedControl(
        options = currencies.map { currency ->
            NeoSegmentOption(
                key = currency.code.value,
                label = currency.code.value,
            )
        },
        selectedKey = state.selectedCurrencyCode.orEmpty(),
        onSelectionChange = { code ->
            onAction(AccountsAction.CurrencySelected(code))
        },
        modifier = Modifier.fillMaxWidth(),
    )

    if (state.currencyError != null) {
        NeoText(
            text = state.currencyError,
            color = NeoTheme.colors.negative,
        )
    }
}