package com.kswallet.feature.reports.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kswallet.core.designsystem.component.KsWalletEmptyState
import com.kswallet.core.designsystem.component.KsWalletErrorState
import com.kswallet.core.designsystem.component.KsWalletLoadingState
import com.kswallet.core.designsystem.component.KsWalletScreenScaffold
import com.kswallet.core.designsystem.component.KsWalletSectionHeader
import com.kswallet.core.designsystem.icon.KsWalletIcons
import com.kswallet.core.designsystem.theme.KsWalletSpacing
import com.kswallet.core.model.Money
import com.kswallet.core.ui.components.LaunchMessage
import com.kswallet.core.ui.screen.ObserveAsEvents
import com.kswallet.core.ui.util.DateFormatter
import com.kswallet.core.ui.util.MoneyFormatter
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.core.ui.util.onColorFor
import com.kswallet.core.ui.util.toComposeColor
import com.kswallet.feature.reports.R
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalDate
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Reports: one month of the ledger, explained.
 *
 * The screen is a pure function of [ReportsState]; the only thing it decides is how to draw
 * it. The chart is deliberately a row of boxes rather than a chart library - one dependency
 * less, and a bar chart of a month is exactly what `weight` and `height` are for.
 */
@Composable
fun ReportsRoot(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var message by remember { mutableStateOf<UiMessage?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ReportsEvent.ShowMessage -> message = event.message
        }
    }

    snackbarHostState.LaunchMessage(message) { message = null }

    ReportsScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
internal fun ReportsScreen(
    state: ReportsState,
    onAction: (ReportsAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
) {
    KsWalletScreenScaffold(
        title = stringResource(R.string.reports_title),
        onNavigateBack = null, // a top-level tab: the bottom bar is the way out
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { padding ->
        when {
            state.isLoading -> KsWalletLoadingState(modifier = Modifier.padding(padding))

            state.hasFailed -> KsWalletErrorState(
                message = stringResource(R.string.reports_error_load),
                onRetry = { onAction(ReportsAction.RetryClicked) },
                modifier = Modifier.padding(padding),
            )

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = KsWalletSpacing.xl),
                verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
            ) {
                item(key = "month") {
                    MonthPicker(
                        state = state,
                        onAction = onAction,
                        modifier = Modifier.padding(horizontal = KsWalletSpacing.screenHorizontal),
                    )
                }

                if (state.isEmpty) {
                    item(key = "empty") {
                        KsWalletEmptyState(
                            title = stringResource(R.string.reports_empty_title),
                            message = stringResource(R.string.reports_empty_message, state.monthLabel()),
                            icon = KsWalletIcons.reports,
                            modifier = Modifier.padding(horizontal = KsWalletSpacing.sm),
                        )
                    }
                } else {
                    item(key = "summary") {
                        SummaryCard(
                            state = state,
                            modifier = Modifier.padding(horizontal = KsWalletSpacing.screenHorizontal),
                        )
                    }

                    item(key = "comparison") {
                        ComparisonLine(
                            state = state,
                            modifier = Modifier.padding(horizontal = KsWalletSpacing.screenHorizontal),
                        )
                    }

                    item(key = "daily-header") {
                        KsWalletSectionHeader(text = stringResource(R.string.reports_daily_title))
                    }

                    item(key = "daily-chart") {
                        DailySpendChart(
                            days = state.dailyExpenses,
                            modifier = Modifier.padding(horizontal = KsWalletSpacing.screenHorizontal),
                        )
                    }

                    item(key = "categories-header") {
                        KsWalletSectionHeader(text = stringResource(R.string.reports_where_it_went))
                    }

                    items(items = state.categories, key = { it.key() }) { row ->
                        CategoryShareRow(
                            row = row,
                            modifier = Modifier.padding(horizontal = KsWalletSpacing.screenHorizontal),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthPicker(
    state: ReportsState,
    onAction: (ReportsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
    ) {
        IconButton(onClick = { onAction(ReportsAction.PreviousMonthClicked) }) {
            Icon(
                imageVector = KsWalletIcons.chevronLeft,
                contentDescription = stringResource(R.string.reports_previous_month),
            )
        }
        Text(
            text = state.monthLabel(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
        )
        IconButton(
            onClick = { onAction(ReportsAction.NextMonthClicked) },
            enabled = state.canGoNext,
        ) {
            Icon(
                imageVector = KsWalletIcons.chevronRight,
                contentDescription = stringResource(R.string.reports_next_month),
            )
        }
    }
}

@Composable
private fun SummaryCard(state: ReportsState, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KsWalletSpacing.md),
            verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.xs),
        ) {
            Text(text = stringResource(R.string.reports_spent), style = MaterialTheme.typography.labelLarge)
            state.expense?.let { expense ->
                Text(text = MoneyFormatter.format(expense), style = MaterialTheme.typography.headlineMedium)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.lg)) {
                state.income?.let { income ->
                    LabeledAmount(label = stringResource(R.string.reports_income), amount = income)
                }
                state.net?.let { net ->
                    LabeledAmount(
                        label = stringResource(R.string.reports_net),
                        amount = net,
                        showSign = true,
                    )
                }
            }

            state.averagePerDay?.takeIf { !it.isZero }?.let { average ->
                Text(
                    text = stringResource(R.string.reports_average_per_day, MoneyFormatter.format(average)),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            if (state.isCurrentMonth) {
                Text(
                    text = stringResource(
                        R.string.reports_in_progress,
                        state.daysElapsed,
                        state.month.daysInMonth,
                    ),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun LabeledAmount(label: String, amount: Money, showSign: Boolean = false) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(
            text = if (showSign) MoneyFormatter.formatSigned(amount) else MoneyFormatter.format(amount),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

/**
 * "24% more than last month" - the wording carries the direction, and the colour only
 * reinforces it, so the line still reads correctly in greyscale.
 */
@Composable
private fun ComparisonLine(state: ReportsState, modifier: Modifier = Modifier) {
    val change = state.changeVsPreviousMonth
    val text = when {
        change == null -> stringResource(R.string.reports_no_comparison)
        change.absoluteValue < SAME_PERCENT_THRESHOLD -> stringResource(R.string.reports_change_same)
        change > 0f -> stringResource(R.string.reports_change_up, change.percent())
        else -> stringResource(R.string.reports_change_down, change.absoluteValue.percent())
    }
    val color = when {
        change == null || change.absoluteValue < SAME_PERCENT_THRESHOLD -> MaterialTheme.colorScheme.onSurfaceVariant
        change > 0f -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        modifier = modifier,
    )
}

/**
 * The month as bars: one bar per day, zero days drawn as a baseline so the gaps are visible.
 * Heights are relative to the busiest day, which is the only scale that makes a month of
 * spending legible on a phone.
 */
@Composable
private fun DailySpendChart(days: List<Money>, modifier: Modifier = Modifier) {
    val busiest = days.maxOfOrNull { it.minorUnits } ?: 0L

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(ChartHeight),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(BarSpacing),
    ) {
        days.forEach { day ->
            val fraction = if (busiest <= 0L) 0f else day.minorUnits.toFloat() / busiest.toFloat()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(if (fraction <= 0f) BaselineHeight else ChartHeight * max(fraction, MinBarFraction))
                    .background(
                        color = if (day.isZero) {
                            MaterialTheme.colorScheme.surfaceVariant
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp),
                    ),
            )
        }
    }
}

@Composable
private fun CategoryShareRow(row: CategoryShareUi, modifier: Modifier = Modifier) {
    val accent = row.colorArgb?.toComposeColor() ?: MaterialTheme.colorScheme.secondaryContainer

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = KsWalletSpacing.xs),
        verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.xs),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(color = accent, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = KsWalletIcons.byKey(row.iconKey ?: KsWalletIcons.fallbackKey),
                    contentDescription = null,
                    tint = accent.onColorFor(),
                    modifier = Modifier.size(18.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = row.name ?: stringResource(R.string.reports_uncategorised),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = pluralStringResource(
                        R.plurals.reports_entry_count,
                        row.transactionCount,
                        row.transactionCount,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(text = MoneyFormatter.format(row.amount), style = MaterialTheme.typography.bodyLarge)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
        ) {
            LinearProgressIndicator(
                progress = { row.share },
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.reports_share_of_spending, (row.share * 100f).roundToInt()),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** `October 2026`, in the user's language. */
@Composable
private fun ReportsState.monthLabel(): String = DateFormatter.monthAndYear(
    LocalDate.of(month.year, month.month, 1),
)

/** A stable key for the list: the category id, or the uncategorized bucket. */
private fun CategoryShareUi.key(): String = categoryId?.toString() ?: "uncategorised"

private fun Float.percent(): String = String.format(Locale.getDefault(), "%.0f%%", this * 100f)

/** Anything under this is reported as "about the same" rather than as noise. */
private const val SAME_PERCENT_THRESHOLD = 0.005f

private val ChartHeight = 72.dp
private val BaselineHeight = 2.dp
private val BarSpacing = 2.dp

/** A day with spending always shows a visible bar, however small its share of a big month. */
private const val MinBarFraction = 0.06f
