package com.kswallet.feature.categories.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
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
import com.kswallet.core.designsystem.component.KsWalletConfirmDialog
import com.kswallet.core.designsystem.component.KsWalletEmptyState
import com.kswallet.core.designsystem.component.KsWalletLoadingState
import com.kswallet.core.designsystem.component.KsWalletScreenScaffold
import com.kswallet.core.designsystem.icon.KsWalletIcons
import com.kswallet.core.designsystem.theme.KsWalletSpacing
import com.kswallet.core.model.CategoryId
import com.kswallet.core.model.CategoryType
import com.kswallet.core.ui.components.LaunchMessage
import com.kswallet.core.ui.screen.ObserveAsEvents
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.core.ui.util.onColorFor
import com.kswallet.core.ui.util.toComposeColor
import com.kswallet.feature.categories.R
import org.koin.compose.viewmodel.koinViewModel

/**
 * Stateful entry point: it wires the ViewModel to the stateless UI and nothing else.
 */
@Composable
fun CategoriesRoot(
    onAddCategory: () -> Unit,
    onEditCategory: (CategoryId) -> Unit,
    viewModel: CategoriesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var message by remember { mutableStateOf<UiMessage?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CategoriesEvent.OpenEditor ->
                if (event.categoryId == null) onAddCategory() else onEditCategory(event.categoryId)

            is CategoriesEvent.ShowMessage -> message = event.message
        }
    }

    snackbarHostState.LaunchMessage(message) { message = null }

    CategoriesScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
    )
}

/** Stateless screen: a pure function of [state], which is what makes it previewable. */
@Composable
internal fun CategoriesScreen(
    state: CategoriesState,
    onAction: (CategoriesAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
) {
    KsWalletScreenScaffold(
        title = stringResource(R.string.categories_title),
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(CategoriesAction.AddCategoryClicked) }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.categories_add))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            PrimaryTabRow(selectedTabIndex = if (state.selectedType == CategoryType.EXPENSE) 0 else 1) {
                Tab(
                    selected = state.selectedType == CategoryType.EXPENSE,
                    onClick = { onAction(CategoriesAction.TypeSelected(CategoryType.EXPENSE)) },
                    text = { Text(stringResource(R.string.category_type_expense)) },
                )
                Tab(
                    selected = state.selectedType == CategoryType.INCOME,
                    onClick = { onAction(CategoriesAction.TypeSelected(CategoryType.INCOME)) },
                    text = { Text(stringResource(R.string.category_type_income)) },
                )
            }

            when {
                state.isLoading -> KsWalletLoadingState()

                state.isEmpty -> KsWalletEmptyState(
                    title = stringResource(R.string.categories_empty_title),
                    message = stringResource(R.string.categories_empty_message),
                    icon = KsWalletIcons.byKey(KsWalletIcons.fallbackKey),
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = KsWalletSpacing.sm),
                ) {
                    items(items = state.categories, key = { it.id.value }) { row ->
                        CategoryRow(
                            row = row,
                            onClick = { onAction(CategoriesAction.CategoryClicked(row.id)) },
                            onDelete = { onAction(CategoriesAction.DeleteClicked(row.id)) },
                        )
                    }
                }
            }
        }
    }

    state.pendingDelete?.let { pending ->
        KsWalletConfirmDialog(
            title = stringResource(R.string.category_delete_title, pending.name),
            message = if (pending.transactionCount == 0) {
                stringResource(R.string.category_delete_message)
            } else {
                pluralStringResource(
                    R.plurals.category_delete_message_with_transactions,
                    pending.transactionCount,
                    pending.transactionCount,
                )
            },
            confirmLabel = stringResource(R.string.action_delete),
            onConfirm = { onAction(CategoriesAction.DeleteConfirmed) },
            onDismiss = { onAction(CategoriesAction.DeleteDismissed) },
            destructive = true,
        )
    }
}

@Composable
private fun CategoryRow(
    row: CategoryRowUi,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val color = row.colorArgb.toComposeColor()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = KsWalletSpacing.screenHorizontal, vertical = KsWalletSpacing.listItemVertical),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color = color, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = KsWalletIcons.byKey(row.iconKey),
                contentDescription = null,
                tint = color.onColorFor(),
                modifier = Modifier.size(22.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = row.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = pluralStringResource(
                    R.plurals.category_transaction_count,
                    row.transactionCount,
                    row.transactionCount,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
        }
    }
}
