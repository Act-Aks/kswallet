package com.kswallet.feature.accounts.presentation.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kswallet.core.designsystem.component.KsWalletLoadingState
import com.kswallet.core.designsystem.component.KsWalletScreenScaffold
import com.kswallet.core.designsystem.theme.KsWalletAccentPalette
import com.kswallet.core.designsystem.theme.KsWalletSpacing
import com.kswallet.core.model.AccountType
import com.kswallet.core.model.Currency
import com.kswallet.core.ui.components.LaunchMessage
import com.kswallet.core.ui.components.asText
import com.kswallet.core.ui.screen.ObserveAsEvents
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.core.ui.util.toComposeColor
import com.kswallet.feature.accounts.R
import com.kswallet.feature.accounts.presentation.error.toUiMessage
import com.kswallet.feature.accounts.presentation.util.icon
import com.kswallet.feature.accounts.presentation.util.label
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AccountEditorRoot(
    accountIdValue: Long,
    onNavigateBack: () -> Unit,
    viewModel: AccountEditorViewModel = koinViewModel(parameters = { parametersOf(accountIdValue) }),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var message by remember { mutableStateOf<UiMessage?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is AccountEditorEvent.NavigateBack -> onNavigateBack()
            is AccountEditorEvent.ShowMessage -> message = event.message
        }
    }

    snackbarHostState.LaunchMessage(message) { message = null }

    AccountEditorScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEditorScreen(
    state: AccountEditorState,
    onAction: (AccountEditorAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
) {
    KsWalletScreenScaffold(
        title = stringResource(
            if (state.isNew) R.string.account_editor_title_new else R.string.account_editor_title_edit,
        ),
        onNavigateBack = { onAction(AccountEditorAction.BackClicked) },
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        if (state.isLoading) {
            KsWalletLoadingState(modifier = Modifier.padding(padding))
            return@KsWalletScreenScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = KsWalletSpacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.md),
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { onAction(AccountEditorAction.NameChanged(it)) },
                label = { Text(stringResource(R.string.account_field_name)) },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { error -> { Text(error.toUiMessage().asText()) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(text = stringResource(R.string.account_field_type), style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm)) {
                AccountType.entries.take(3).forEach { type ->
                    FilterChip(
                        selected = state.type == type,
                        onClick = { onAction(AccountEditorAction.TypeChanged(type)) },
                        leadingIcon = { androidx.compose.material3.Icon(type.icon(), contentDescription = null) },
                        label = { Text(type.label()) },
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm)) {
                AccountType.entries.drop(3).forEach { type ->
                    FilterChip(
                        selected = state.type == type,
                        onClick = { onAction(AccountEditorAction.TypeChanged(type)) },
                        leadingIcon = { androidx.compose.material3.Icon(type.icon(), contentDescription = null) },
                        label = { Text(type.label()) },
                    )
                }
            }

            var currencyExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = currencyExpanded,
                onExpandedChange = { currencyExpanded = it },
            ) {
                OutlinedTextField(
                    value = "${state.currency.code} (${state.currency.symbol})",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.account_field_currency)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(androidx.compose.material3.ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                )
                ExposedDropdownMenu(expanded = currencyExpanded, onDismissRequest = { currencyExpanded = false }) {
                    Currency.entries.forEach { currency ->
                        DropdownMenuItem(
                            text = { Text("${currency.code} (${currency.symbol})") },
                            onClick = {
                                onAction(AccountEditorAction.CurrencyChanged(currency))
                                currencyExpanded = false
                            },
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.initialBalanceText,
                onValueChange = { onAction(AccountEditorAction.InitialBalanceChanged(it)) },
                label = { Text(stringResource(R.string.account_field_initial_balance)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.balanceError != null,
                supportingText = state.balanceError?.let { error -> { Text(error.toUiMessage().asText()) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.account_field_included_in_total),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Switch(
                    checked = state.includedInTotal,
                    onCheckedChange = { onAction(AccountEditorAction.IncludedInTotalChanged(it)) },
                )
            }

            Text(text = stringResource(R.string.account_field_color), style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm)) {
                KsWalletAccentPalette.take(6).forEach { colorValue ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(color = colorValue.toComposeColor(), shape = CircleShape)
                            .border(
                                width = if (colorValue == state.colorArgb) 3.dp else 0.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape,
                            )
                            .clickable { onAction(AccountEditorAction.ColorChanged(colorValue)) },
                    )
                }
            }

            Button(
                onClick = { onAction(AccountEditorAction.SaveClicked) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                } else {
                    Text(stringResource(R.string.action_save))
                }
            }
        }
    }
}
