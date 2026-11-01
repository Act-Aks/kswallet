package com.kswallet.feature.accounts.presentation.create_account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import com.kswallet.core.screen.ObserveAsEvents
import com.kswallet.feature.accounts.presentation.R
import com.neodesign.component.NeoButton
import com.neodesign.component.NeoSegmentOption
import com.neodesign.component.NeoSegmentedControl
import com.neodesign.component.NeoText
import com.neodesign.component.NeoTextField
import com.neodesign.theme.NeoTheme
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateAccountRoot(
    onAccountCreated: () -> Unit,
    viewModel: CreateAccountViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currencyRegistry = koinInject<CurrencyRegistry>()

    ObserveAsEvents(events = viewModel.events) { event ->
        when (event) {
            CreateAccountEvent.AccountCreated -> onAccountCreated()
        }
    }

    NeoTheme {
        CreateAccountScreen(
            state = state,
            currencies = currencyRegistry.supportedCurrencies(),
            onAction = viewModel::onAction,
        )
    }
}

@Composable
private fun CreateAccountScreen(
    state: CreateAccountState,
    currencies: List<CurrencyUnit>,
    onAction: (CreateAccountAction) -> Unit,
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
            text = stringResource(R.string.accounts_create_title),
            style = NeoTheme.typography.display,
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

        NeoButton(
            text = stringResource(R.string.accounts_create_submit),
            onClick = {
                onAction(CreateAccountAction.SubmitClicked)
            },
            enabled = state.canSubmit,
            loading = state.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun NameField(
    state: CreateAccountState,
    onAction: (CreateAccountAction) -> Unit,
) {
    val nameState = rememberTextFieldState(
        initialText = state.name,
    )

    LaunchedEffect(nameState) {
        snapshotFlow { nameState.text.toString() }
            .collect { value ->
                onAction(CreateAccountAction.NameChanged(value))
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
    onAction: (CreateAccountAction) -> Unit,
) {
    val selectedCurrencyCode = state.selectedCurrencyCode ?: return

    NeoSegmentedControl(
        options = currencies.map { currency ->
            NeoSegmentOption(
                key = currency.code.value,
                label = currency.code.value,
            )
        },
        selectedKey = selectedCurrencyCode,
        onSelectionChange = { code ->
            onAction(CreateAccountAction.CurrencySelected(code))
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