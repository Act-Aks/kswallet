package com.kswallet.feature.categories.presentation.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kswallet.core.designsystem.component.KsWalletLoadingState
import com.kswallet.core.designsystem.component.KsWalletScreenScaffold
import com.kswallet.core.designsystem.icon.KsWalletIcons
import com.kswallet.core.designsystem.theme.KsWalletAccentPalette
import com.kswallet.core.designsystem.theme.KsWalletSpacing
import com.kswallet.core.model.CategoryType
import com.kswallet.core.ui.components.LaunchMessage
import com.kswallet.core.ui.components.asText
import com.kswallet.core.ui.screen.ObserveAsEvents
import com.kswallet.core.ui.util.UiMessage
import com.kswallet.core.ui.util.onColorFor
import com.kswallet.core.ui.util.toComposeColor
import com.kswallet.feature.categories.R
import com.kswallet.feature.categories.presentation.error.toUiMessage
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CategoryEditorRoot(
    categoryIdValue: Long,
    onNavigateBack: () -> Unit,
    viewModel: CategoryEditorViewModel = koinViewModel(parameters = { parametersOf(categoryIdValue) }),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var message by remember { mutableStateOf<UiMessage?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CategoryEditorEvent.NavigateBack -> onNavigateBack()
            is CategoryEditorEvent.ShowMessage -> message = event.message
        }
    }

    snackbarHostState.LaunchMessage(message) { message = null }

    CategoryEditorScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
internal fun CategoryEditorScreen(
    state: CategoryEditorState,
    onAction: (CategoryEditorAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
) {
    KsWalletScreenScaffold(
        title = if (state.isNew) {
            stringResource(R.string.category_editor_title_new)
        } else {
            stringResource(R.string.category_editor_title_edit)
        },
        onNavigateBack = { onAction(CategoryEditorAction.BackClicked) },
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
                .padding(horizontal = KsWalletSpacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.md),
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { onAction(CategoryEditorAction.NameChanged(it)) },
                label = { Text(stringResource(R.string.category_field_name)) },
                isError = state.error != null,
                supportingText = state.error?.let { error -> { Text(error.toUiMessage().asText()) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm)) {
                CategoryType.entries.forEach { type ->
                    FilterChip(
                        selected = state.type == type,
                        onClick = { onAction(CategoryEditorAction.TypeChanged(type)) },
                        label = {
                            Text(
                                stringResource(
                                    if (type == CategoryType.EXPENSE) R.string.category_type_expense else R.string.category_type_income,
                                ),
                            )
                        },
                    )
                }
            }

            Text(text = stringResource(R.string.category_field_icon), style = MaterialTheme.typography.titleSmall)
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                contentPadding = PaddingValues(vertical = KsWalletSpacing.xs),
                horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(items = KsWalletIcons.all, key = { it.key }) { icon ->
                    val selected = icon.key == state.iconKey
                    val background = state.colorArgb.toComposeColor()
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = if (selected) background else MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape,
                            )
                            .border(
                                width = if (selected) 2.dp else 0.dp,
                                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape,
                            )
                            .clickable { onAction(CategoryEditorAction.IconChanged(icon.key)) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = icon.icon,
                            contentDescription = icon.key,
                            tint = if (selected) background.onColorFor() else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Text(text = stringResource(R.string.category_field_color), style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(KsWalletSpacing.sm)) {
                KsWalletAccentPalette.forEach { colorValue ->
                    val selected = colorValue == state.colorArgb
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(color = colorValue.toComposeColor(), shape = CircleShape)
                            .border(
                                width = if (selected) 3.dp else 0.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape,
                            )
                            .clickable { onAction(CategoryEditorAction.ColorChanged(colorValue)) },
                    )
                }
            }

            Button(
                onClick = { onAction(CategoryEditorAction.SaveClicked) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                } else {
                    Text(stringResource(R.string.action_save))
                }
            }
        }
    }
}
