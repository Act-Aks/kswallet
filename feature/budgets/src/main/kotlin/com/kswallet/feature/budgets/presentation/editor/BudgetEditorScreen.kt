package com.kswallet.feature.budgets.presentation.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kswallet.core.designsystem.component.KsWalletLoadingState
import com.kswallet.core.designsystem.component.KsWalletScreenScaffold
import com.kswallet.core.designsystem.theme.KsWalletSpacing
import com.kswallet.core.ui.components.LaunchMessage
import com.kswallet.core.ui.components.asText
import com.kswallet.core.ui.screen.ObserveAsEvents
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.budgets.R
import com.kswallet.feature.budgets.domain.model.BudgetPeriod
import com.kswallet.feature.budgets.presentation.error.toUiMessage
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BudgetEditorRoute(
    budgetIdValue: Long,
    onNavigateBack: () -> Unit,
    viewModel: BudgetEditorViewModel = koinViewModel(parameters = { parametersOf(budgetIdValue) }),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var message by remember { mutableStateOf<UiMessage?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is BudgetEditorEvent.NavigateBack -> onNavigateBack()
            is BudgetEditorEvent.ShowMessage -> message = event.message
        }
    }

    snackbarHostState.LaunchMessage(message) { message = null }

    BudgetEditorScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BudgetEditorScreen(
    state: BudgetEditorState,
    onAction: (BudgetEditorAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
) {
    KsWalletScreenScaffold(
        title = if (state.isNew) {
            stringResource(R.string.budget_editor_title_new)
        } else {
            stringResource(R.string.budget_editor_title_edit)
        },
        onNavigateBack = { onAction(BudgetEditorAction.BackClicked) },
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
            var categoryExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it },
            ) {
                OutlinedTextField(
                    value = state.categoryName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.budget_field_category)) },
                    placeholder = { Text(stringResource(R.string.budget_field_category_hint)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    isError = state.error == BudgetEditorError.CategoryMissing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                ) {
                    state.categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                onAction(BudgetEditorAction.CategorySelected(category.id))
                                categoryExpanded = false
                            },
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.limitText,
                onValueChange = { onAction(BudgetEditorAction.LimitChanged(it)) },
                label = { Text(stringResource(R.string.budget_field_limit, state.currency.code)) },
                prefix = { Text(state.currency.symbol) },
                isError = state.error != null && state.error != BudgetEditorError.CategoryMissing,
                supportingText = state.error
                    ?.takeIf { it != BudgetEditorError.CategoryMissing }
                    ?.let { error -> { Text(error.toUiMessage().asText()) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = stringResource(R.string.budget_field_period),
                style = MaterialTheme.typography.titleSmall,
            )
            androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm)) {
                BudgetPeriod.entries.forEach { period ->
                    FilterChip(
                        selected = period == state.period,
                        onClick = { onAction(BudgetEditorAction.PeriodChanged(period)) },
                        label = { Text(stringResource(period.labelRes())) },
                    )
                }
            }

            Button(
                onClick = { onAction(BudgetEditorAction.SaveClicked) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    }
}

/** The period's name, and also the words the list uses for "spent this …". */
private fun BudgetPeriod.labelRes(): Int = when (this) {
    BudgetPeriod.WEEKLY -> R.string.budget_period_weekly
    BudgetPeriod.MONTHLY -> R.string.budget_period_monthly
    BudgetPeriod.YEARLY -> R.string.budget_period_yearly
}
