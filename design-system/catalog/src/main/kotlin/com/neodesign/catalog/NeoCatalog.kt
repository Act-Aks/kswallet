package com.neodesign.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.neodesign.component.NeoButton
import com.neodesign.component.NeoButtonVariant
import com.neodesign.component.NeoIconButton
import com.neodesign.component.NeoSurface
import com.neodesign.component.NeoText
import com.neodesign.theme.NeoTheme

private enum class CatalogThemeMode {
    System,
    Light,
    Dark,
}

@Composable
internal fun NeoCatalog(
    onDarkThemeChanged: @Composable (Boolean) -> Unit,
) {
    var modeName by rememberSaveable {
        mutableStateOf(CatalogThemeMode.System.name)
    }

    val mode = CatalogThemeMode.valueOf(modeName)

    val darkTheme = when (mode) {
        CatalogThemeMode.System -> isSystemInDarkTheme()
        CatalogThemeMode.Light -> false
        CatalogThemeMode.Dark -> true
    }

    onDarkThemeChanged(darkTheme)

    NeoTheme(darkTheme = darkTheme) {
        CatalogContent(
            selectedMode = mode,
            onModeSelected = { modeName = it.name },
        )
    }
}

@Composable
private fun CatalogContent(
    selectedMode: CatalogThemeMode,
    onModeSelected: (CatalogThemeMode) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoTheme.colors.background)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(NeoTheme.spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(
            NeoTheme.spacing.extraLarge,
        ),
    ) {
        NeoText(
            text = "NeoDesign",
            style = NeoTheme.typography.display,
        )

        NeoText(
            text = "An independent Compose design library.",
            color = NeoTheme.colors.contentSecondary,
        )

        ThemeSelector(
            selectedMode = selectedMode,
            onModeSelected = onModeSelected,
        )

        ButtonsSection()
        TypographySection()
        SurfaceSection()
        SemanticColorsSection()
    }
}

@Composable
private fun ThemeSelector(
    selectedMode: CatalogThemeMode,
    onModeSelected: (CatalogThemeMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(
            NeoTheme.spacing.small,
        ),
    ) {
        CatalogThemeMode.entries.forEach { mode ->
            val selected = mode == selectedMode

            val background = if (selected) {
                NeoTheme.colors.action
            } else {
                NeoTheme.colors.surfaceRaised
            }

            val foreground = if (selected) {
                NeoTheme.colors.onAction
            } else {
                NeoTheme.colors.contentPrimary
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(NeoTheme.shapes.pill)
                    .background(background)
                    .selectable(
                        selected = selected,
                        role = Role.RadioButton,
                        onClick = { onModeSelected(mode) },
                    )
                    .defaultMinSize(minHeight = 48.dp)
                    .padding(NeoTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                NeoText(
                    text = mode.name,
                    color = foreground,
                    style = NeoTheme.typography.label,
                )
            }
        }
    }
}

@Composable
private fun ButtonsSection() {
    var clicks by rememberSaveable { mutableStateOf(0) }
    var loading by remember { mutableStateOf(false) }

    CatalogSection(title = "Buttons") {
        NeoText(
            text = "Actions received: $clicks",
            color = NeoTheme.colors.contentSecondary,
        )

        NeoButton(
            text = "Primary action",
            modifier = Modifier.fillMaxWidth(),
            onClick = { clicks++ },
        )

        NeoButton(
            text = "Secondary action",
            modifier = Modifier.fillMaxWidth(),
            variant = NeoButtonVariant.Secondary,
            onClick = { clicks++ },
        )

        NeoButton(
            text = "Outlined action",
            modifier = Modifier.fillMaxWidth(),
            variant = NeoButtonVariant.Outlined,
            onClick = { clicks++ },
        )

        NeoButton(
            text = "Delete item",
            modifier = Modifier.fillMaxWidth(),
            variant = NeoButtonVariant.Destructive,
            onClick = { clicks++ },
        )

        NeoButton(
            text = "Disabled action",
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            onClick = { clicks++ },
        )

        NeoButton(
            text = "Save changes",
            modifier = Modifier.fillMaxWidth(),
            loading = loading,
            onClick = { loading = true },
        )

        NeoButton(
            text = if (loading) {
                "Finish simulated loading"
            } else {
                "Start simulated loading"
            },
            modifier = Modifier.fillMaxWidth(),
            variant = NeoButtonVariant.Outlined,
            onClick = { loading = !loading },
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(
                NeoTheme.spacing.medium,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NeoIconButton(
                contentDescription = "Add demo item",
                onClick = { clicks++ },
            ) {
                NeoText(
                    text = "+",
                    style = NeoTheme.typography.title,
                )
            }

            NeoIconButton(
                contentDescription = "Add demo item",
                variant = NeoButtonVariant.Primary,
                onClick = { clicks++ },
            ) {
                NeoText(
                    text = "+",
                    style = NeoTheme.typography.title,
                )
            }

            NeoIconButton(
                contentDescription = "Add demo item",
                enabled = false,
                onClick = { clicks++ },
            ) {
                NeoText(
                    text = "+",
                    style = NeoTheme.typography.title,
                )
            }
        }
    }
}

@Composable
private fun TypographySection() {
    CatalogSection(title = "Typography") {
        NeoText("Display", style = NeoTheme.typography.display)
        NeoText("Headline", style = NeoTheme.typography.headline)
        NeoText("Title", style = NeoTheme.typography.title)
        NeoText("Body text for readable content.")
        NeoText("Label", style = NeoTheme.typography.label)
        NeoText(
            text = "Caption and supporting information",
            style = NeoTheme.typography.caption,
            color = NeoTheme.colors.contentSecondary,
        )
    }
}

@Composable
private fun SurfaceSection() {
    CatalogSection(title = "Surfaces") {
        NeoSurface(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, NeoTheme.colors.outline),
        ) {
            Column(
                modifier = Modifier.padding(
                    NeoTheme.spacing.extraLarge,
                ),
                verticalArrangement = Arrangement.spacedBy(
                    NeoTheme.spacing.small,
                ),
            ) {
                NeoText(
                    text = "Standard surface",
                    style = NeoTheme.typography.title,
                )

                NeoText(
                    text = "Neutral, rounded, and independent of any app.",
                    color = NeoTheme.colors.contentSecondary,
                )
            }
        }

        NeoSurface(
            modifier = Modifier.fillMaxWidth(),
            color = NeoTheme.colors.surfaceRaised,
        ) {
            NeoText(
                text = "Raised tonal surface",
                modifier = Modifier.padding(
                    NeoTheme.spacing.extraLarge,
                ),
            )
        }

        NeoSurface(
            modifier = Modifier.fillMaxWidth(),
            color = NeoTheme.colors.action,
            contentColor = NeoTheme.colors.onAction,
        ) {
            NeoText(
                text = "Inherited contrasting content",
                modifier = Modifier.padding(
                    NeoTheme.spacing.extraLarge,
                ),
                style = NeoTheme.typography.label,
            )
        }
    }
}

@Composable
private fun SemanticColorsSection() {
    CatalogSection(title = "Semantic content") {
        NeoText("Accent", color = NeoTheme.colors.accent)
        NeoText("Positive", color = NeoTheme.colors.positive)
        NeoText("Warning", color = NeoTheme.colors.warning)
        NeoText("Negative", color = NeoTheme.colors.negative)
    }
}

@Composable
private fun CatalogSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            NeoTheme.spacing.medium,
        ),
    ) {
        NeoText(
            text = title,
            style = NeoTheme.typography.title,
        )

        content()
    }
}