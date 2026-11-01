package com.kswallet.feature.accounts.presentation.edit_account

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
import com.kswallet.feature.accounts.domain.model.AccountId
import com.kswallet.feature.accounts.presentation.R
import com.neodesign.component.NeoButton
import com.neodesign.component.NeoButtonVariant
import com.neodesign.component.NeoCard
import com.neodesign.component.NeoText
import com.neodesign.component.NeoTextField
import com.neodesign.theme.NeoTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditAccountRoot(
    accountId: String,
    onAccountUpdated: () -> Unit,
    viewModel: EditAccountViewModel = koinViewModel {
        parametersOf(AccountId.parse(accountId))
    },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                EditAccountEvent.AccountUpdated -> onAccountUpdated()
            }
        }
    }

    NeoTheme {
        EditAccountScreen(
            state = state,
            onAction = viewModel::onAction,
        )
    }
}

@Composable
private fun EditAccountScreen(
    state: EditAccountState,
    onAction: (EditAccountAction) -> Unit,
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
            text = stringResource(R.string.accounts_edit_title),
            style = NeoTheme.typography.display,
        )

        if (state.isLoading) {
            NeoCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                NeoText(
                    text = stringResource(R.string.accounts_loading),
                    style = NeoTheme.typography.headline,
                )
            }
            return
        }

        if (state.actionError != null) {
            NeoText(
                text = state.actionError,
                color = NeoTheme.colors.negative,
            )
        }

        NameField(
            state = state,
            onAction = onAction,
        )

        NeoText(
            text = state.currencyCode,
            color = NeoTheme.colors.contentSecondary,
        )

        NeoButton(
            text = stringResource(R.string.accounts_save),
            onClick = {
                onAction(EditAccountAction.SaveClicked)
            },
            enabled = state.canSave,
            loading = state.isSaving,
            modifier = Modifier.fillMaxWidth(),
        )

        NeoButton(
            text = if (state.isArchived) {
                stringResource(R.string.accounts_unarchive)
            } else {
                stringResource(R.string.accounts_archive)
            },
            onClick = {
                onAction(EditAccountAction.ArchiveToggleClicked)
            },
            variant = NeoButtonVariant.Outlined,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun NameField(
    state: EditAccountState,
    onAction: (EditAccountAction) -> Unit,
) {
    val nameState = rememberTextFieldState(
        initialText = state.name,
    )

    LaunchedEffect(nameState) {
        snapshotFlow { nameState.text.toString() }
            .collect { value ->
                onAction(EditAccountAction.NameChanged(value))
            }
    }

    NeoTextField(
        state = nameState,
        label = stringResource(R.string.accounts_create_name_label),
        errorMessage = state.nameError,
        modifier = Modifier.fillMaxWidth(),
    )
}