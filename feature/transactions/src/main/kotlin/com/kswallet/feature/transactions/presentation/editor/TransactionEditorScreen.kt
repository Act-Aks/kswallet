package com.kswallet.feature.transactions.presentation.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kswallet.core.designsystem.component.KsWalletLoadingState
import com.kswallet.core.designsystem.component.KsWalletScreenScaffold
import com.kswallet.core.designsystem.theme.KsWalletSpacing
import com.kswallet.core.model.TransactionType
import com.kswallet.core.ui.components.LaunchMessage
import com.kswallet.core.ui.components.asText
import com.kswallet.core.ui.screen.ObserveAsEvents
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.transactions.R
import com.kswallet.feature.transactions.presentation.error.toUiMessage
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TransactionEditorRoute(
    transactionIdValue: Long,
    onNavigateBack: () -> Unit,
    viewModel: TransactionEditorViewModel = koinViewModel(
        parameters = { parametersOf(transactionIdValue) },
    ),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var message by remember { mutableStateOf<UiMessage?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is TransactionEditorEvent.NavigateBack -> onNavigateBack()
            is TransactionEditorEvent.ShowMessage -> message = event.message
        }
    }

    snackbarHostState.LaunchMessage(message) { message = null }

    TransactionEditorScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
internal fun TransactionEditorScreen(
    state: TransactionEditorState,
    onAction: (TransactionEditorAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
) {
    KsWalletScreenScaffold(
        title = stringResource(
            if (state.isNew) R.string.transaction_editor_title_new else R.string.transaction_editor_title_edit,
        ),
        onNavigateBack = { onAction(TransactionEditorAction.BackClicked) },
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
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                TransactionType.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = state.type == type,
                        onClick = { onAction(TransactionEditorAction.TypeChanged(type)) },
                        shape = SegmentedButtonDefaults.itemShape(index, TransactionType.entries.size),
                    ) {
                        Text(type.label())
                    }
                }
            }

            OutlinedTextField(
                value = state.amountText,
                onValueChange = { onAction(TransactionEditorAction.AmountChanged(it)) },
                label = { Text(stringResource(R.string.transaction_field_amount)) },
                prefix = { Text(state.currency.symbol) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.errors.hasAmountError(),
                supportingText = state.errors.amountSupportingText()?.let { text -> { Text(text) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            CatalogDropdown(
                label = stringResource(R.string.transaction_field_account),
                selectedLabel = state.selectedAccount?.name,
                options = state.accounts.map { it.id.value to it.name },
                onSelect = { value ->
                    state.accounts.firstOrNull { it.id.value == value }
                        ?.let { onAction(TransactionEditorAction.AccountSelected(it.id)) }
                },
            )

            if (state.needsTransferTarget) {
                CatalogDropdown(
                    label = stringResource(R.string.transaction_field_transfer_target),
                    selectedLabel = state.accounts.firstOrNull { it.id == state.transferAccountId }?.name,
                    options = state.accounts
                        .filter { it.id != state.accountId }
                        .map { it.id.value to it.name },
                    onSelect = { value ->
                        state.accounts.firstOrNull { it.id.value == value }
                            ?.let { onAction(TransactionEditorAction.TransferAccountSelected(it.id)) }
                    },
                )
            }

            if (state.needsCategory) {
                CatalogDropdown(
                    label = stringResource(R.string.transaction_field_category),
                    selectedLabel = state.categories.firstOrNull { it.id == state.categoryId }?.name,
                    options = state.selectableCategories.map { it.id.value to it.name },
                    onSelect = { value ->
                        state.selectableCategories.firstOrNull { it.id.value == value }
                            ?.let { onAction(TransactionEditorAction.CategorySelected(it.id)) }
                    },
                )
            }

            OutlinedTextField(
                value = state.note,
                onValueChange = { onAction(TransactionEditorAction.NoteChanged(it)) },
                label = { Text(stringResource(R.string.transaction_field_note)) },
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
            ) {
                Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(
                    text = state.dateLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Button(
                onClick = { onAction(TransactionEditorAction.SaveClicked) },
                enabled = state.canSave,
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

/** One reusable "pick from a list" control - accounts, categories and transfer targets all use it. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogDropdown(
    label: String,
    selectedLabel: String?,
    options: List<Pair<Long, String>>,
    onSelect: (Long) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedLabel ?: stringResource(R.string.transaction_field_none),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (value, optionLabel) ->
                DropdownMenuItem(
                    text = { Text(optionLabel) },
                    onClick = {
                        onSelect(value)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun Set<TransactionEditorError>.hasAmountError(): Boolean =
    contains(TransactionEditorError.InvalidAmount) || contains(TransactionEditorError.AmountNotPositive)

@Composable
private fun Set<TransactionEditorError>.amountSupportingText(): String? {
    val error = firstOrNull {
        it == TransactionEditorError.InvalidAmount || it == TransactionEditorError.AmountNotPositive
    } ?: return null
    return error.toUiMessage().asText()
}

@Composable
private fun TransactionType.label(): String = stringResource(
    when (this) {
        TransactionType.INCOME -> R.string.transaction_type_income
        TransactionType.EXPENSE -> R.string.transaction_type_expense
        TransactionType.TRANSFER -> R.string.transaction_type_transfer
    },
)
