package com.kswallet.feature.settings.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kswallet.core.datastore.domain.model.ThemeMode
import com.kswallet.core.designsystem.component.KsWalletLoadingState
import com.kswallet.core.designsystem.component.KsWalletScreenScaffold
import com.kswallet.core.designsystem.component.KsWalletSectionHeader
import com.kswallet.core.designsystem.theme.KsWalletSpacing
import com.kswallet.core.model.Currency
import com.kswallet.core.ui.components.LaunchMessage
import com.kswallet.core.ui.screen.ObserveAsEvents
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.settings.R
import org.koin.compose.viewmodel.koinViewModel

/**
 * Settings.
 *
 * This feature has no `data/` package: the storage it edits is `:core:datastore`, which is
 * already a module of its own, so the feature is domain use cases plus one screen.
 */
@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var message by remember { mutableStateOf<UiMessage?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is SettingsEvent.ShowMessage -> message = event.message

            SettingsEvent.NavigateBack -> onNavigateBack()
        }
    }

    snackbarHostState.LaunchMessage(message) { message = null }

    SettingsScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
internal fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    snackbarHostState: SnackbarHostState? = null,
) {
    KsWalletScreenScaffold(
        title = stringResource(R.string.settings_title),
        onNavigateBack = onNavigateBack,
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
                .padding(horizontal = KsWalletSpacing.screenHorizontal, vertical = KsWalletSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
        ) {
            KsWalletSectionHeader(text = stringResource(R.string.settings_section_appearance))

            ThemeCard(
                selected = state.settings.themeMode,
                onSelect = { onAction(SettingsAction.ThemeSelected(it)) },
            )

            DynamicColorCard(
                enabled = state.settings.dynamicColor,
                onToggle = { onAction(SettingsAction.DynamicColorToggled(it)) },
            )

            KsWalletSectionHeader(text = stringResource(R.string.settings_section_data))

            Text(
                text = stringResource(R.string.settings_default_currency),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = stringResource(R.string.settings_default_currency_note),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(vertical = KsWalletSpacing.xs)) {
                    state.currencies.forEachIndexed { index, currency ->
                        if (index > 0) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = KsWalletSpacing.md))
                        }
                        CurrencyRow(
                            currency = currency,
                            selected = currency == state.settings.defaultCurrency,
                            onSelect = { onAction(SettingsAction.CurrencySelected(currency)) },
                        )
                    }
                }
            }

            KsWalletSectionHeader(text = stringResource(R.string.settings_about))

            Text(
                text = stringResource(R.string.settings_about_note),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = KsWalletSpacing.xl),
            )
        }
    }
}

@Composable
private fun ThemeCard(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KsWalletSpacing.md),
            verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
        ) {
            Text(
                text = stringResource(R.string.settings_theme),
                style = MaterialTheme.typography.bodyLarge,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm)) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = mode == selected,
                        onClick = { onSelect(mode) },
                        label = { Text(text = stringResource(mode.labelRes())) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DynamicColorCard(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KsWalletSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.md),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.settings_dynamic_color),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(R.string.settings_dynamic_color_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(checked = enabled, onCheckedChange = onToggle)
        }
    }
}

@Composable
private fun CurrencyRow(
    currency: Currency,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // The row is the touch target and the radio button is decoration, so a screen
            // reader announces "selected" for the whole row, not for a 48dp circle.
            .selectable(selected = selected, role = Role.RadioButton, onClick = onSelect)
            .padding(horizontal = KsWalletSpacing.md, vertical = KsWalletSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = "${currency.code} · ${currency.symbol}",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

private fun ThemeMode.labelRes(): Int = when (this) {
    ThemeMode.SYSTEM -> R.string.settings_theme_system
    ThemeMode.LIGHT -> R.string.settings_theme_light
    ThemeMode.DARK -> R.string.settings_theme_dark
}
