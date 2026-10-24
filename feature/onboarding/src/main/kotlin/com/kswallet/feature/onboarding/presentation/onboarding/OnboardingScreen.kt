package com.kswallet.feature.onboarding.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kswallet.core.datastore.domain.model.ThemeMode
import com.kswallet.core.designsystem.icon.KsWalletIcons
import com.kswallet.core.designsystem.theme.KsWalletSpacing
import com.kswallet.core.model.Currency
import com.kswallet.core.ui.components.LaunchMessage
import com.kswallet.core.ui.screen.ObserveAsEvents
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.feature.onboarding.R
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnboardingRoot(
    onCompleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var message by remember { mutableStateOf<UiMessage?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            OnboardingEvent.Completed -> onCompleted()
            is OnboardingEvent.ShowMessage -> message = event.message
        }
    }
    snackbarHostState.LaunchMessage(message) { message = null }

    OnboardingScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
internal fun OnboardingScreen(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = KsWalletSpacing.screenHorizontal, vertical = KsWalletSpacing.md),
            verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.md),
        ) {
            Text(
                text = stringResource(R.string.onboarding_step, state.step + 1, ONBOARDING_STEP_COUNT),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(state.step.titleRes()),
                style = MaterialTheme.typography.headlineMedium,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.md),
            ) {
                when (state.step) {
                    0 -> WelcomePage()
                    1 -> CurrencyPage(
                        currencies = state.currencies,
                        selected = state.preferences.defaultCurrency,
                        onSelected = { onAction(OnboardingAction.CurrencySelected(it)) },
                    )

                    else -> AppearancePage(
                        themeMode = state.preferences.themeMode,
                        dynamicColor = state.preferences.dynamicColor,
                        onThemeSelected = { onAction(OnboardingAction.ThemeSelected(it)) },
                        onDynamicColorToggled = { onAction(OnboardingAction.DynamicColorToggled(it)) },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (state.step > 0) {
                    TextButton(
                        onClick = { onAction(OnboardingAction.BackClicked) },
                        enabled = !state.isSaving,
                    ) {
                        Text(stringResource(R.string.onboarding_back))
                    }
                } else {
                    Spacer(modifier = Modifier)
                }

                Button(
                    onClick = {
                        if (state.step == ONBOARDING_STEP_COUNT - 1) {
                            onAction(OnboardingAction.FinishClicked)
                        } else {
                            onAction(OnboardingAction.ContinueClicked)
                        }
                    },
                    enabled = !state.isSaving,
                ) {
                    Text(
                        stringResource(
                            if (state.step == ONBOARDING_STEP_COUNT - 1) {
                                if (state.isSaving) R.string.onboarding_saving else R.string.onboarding_finish
                            } else {
                                R.string.onboarding_continue
                            },
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.md),
    ) {
        Card {
            Icon(
                imageVector = KsWalletIcons.wallet,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(28.dp),
            )
        }
        Text(
            text = stringResource(R.string.onboarding_welcome_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(KsWalletSpacing.xs))
        IntroBenefit(R.string.onboarding_benefit_budget)
        IntroBenefit(R.string.onboarding_benefit_insights)
        IntroBenefit(R.string.onboarding_benefit_private)
    }
}

@Composable
private fun IntroBenefit(textRes: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(KsWalletSpacing.md),
        )
    }
}

@Composable
private fun CurrencyPage(
    currencies: List<Currency>,
    selected: Currency,
    onSelected: (Currency) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm)) {
        Text(
            text = stringResource(R.string.onboarding_currency_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column {
                currencies.forEachIndexed { index, currency ->
                    if (index > 0) HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = currency == selected,
                                role = Role.RadioButton,
                                onClick = { onSelected(currency) },
                            )
                            .padding(
                                horizontal = KsWalletSpacing.md,
                                vertical = KsWalletSpacing.xs,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = currency == selected, onClick = null)
                        Text(
                            text = "${currency.code} · ${currency.symbol}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = KsWalletSpacing.sm),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppearancePage(
    themeMode: ThemeMode,
    dynamicColor: Boolean,
    onThemeSelected: (ThemeMode) -> Unit,
    onDynamicColorToggled: (Boolean) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.md)) {
        Text(
            text = stringResource(R.string.onboarding_appearance_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(KsWalletSpacing.md),
                verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
            ) {
                Text(stringResource(R.string.onboarding_theme_title), style = MaterialTheme.typography.titleMedium)
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = themeMode == mode,
                        onClick = { onThemeSelected(mode) },
                        label = { Text(stringResource(mode.labelRes())) },
                    )
                }
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(KsWalletSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.md),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.onboarding_dynamic_color), style = MaterialTheme.typography.titleSmall)
                    Text(
                        stringResource(R.string.onboarding_dynamic_color_body),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(checked = dynamicColor, onCheckedChange = onDynamicColorToggled)
            }
        }
    }
}

private fun Int.titleRes(): Int = when (this) {
    0 -> R.string.onboarding_welcome_title
    1 -> R.string.onboarding_currency_title
    else -> R.string.onboarding_appearance_title
}

@Composable
private fun ThemeMode.labelRes(): Int = when (this) {
    ThemeMode.SYSTEM -> R.string.onboarding_theme_system
    ThemeMode.LIGHT -> R.string.onboarding_theme_light
    ThemeMode.DARK -> R.string.onboarding_theme_dark
}
