package com.kswallet.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.kswallet.core.designsystem.theme.KsWalletTheme
import com.kswallet.core.designsystem.theme.LocalKsWalletComponentTokens

data class KsWalletTab(
    val route: NavKey,
    @StringRes val label: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

enum class KsWalletBottomBarStyle {
    EXPANDED, // Icon + Label
    COMPACT, // Icon only
}

@Composable
fun KsWalletBottomBar(
    tabs: List<KsWalletTab>,
    selectedRoute: NavKey,
    onSelectRoute: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
    style: KsWalletBottomBarStyle = KsWalletBottomBarStyle.EXPANDED,
) {
    val spacing = KsWalletTheme.spacing
    val tokens = LocalKsWalletComponentTokens.current
    val colorScheme = MaterialTheme.colorScheme
    val hapticFeedback = LocalHapticFeedback.current

    val isDarkTheme = colorScheme.surface.luminance() < 0.5f

    val containerColor = if (isDarkTheme) {
        colorScheme.surface.copy(alpha = tokens.bottomBarContainerAlphaDark)
    } else {
        colorScheme.surface.copy(alpha = tokens.bottomBarContainerAlphaLight)
    }

    val baseBorderColor = if (isDarkTheme) {
        Color.White.copy(alpha = tokens.bottomBarBorderAlphaDark)
    } else {
        Color.Black.copy(alpha = tokens.bottomBarBorderAlphaLight)
    }

    val barShape = MaterialTheme.shapes.extraLarge
    val selectedIndex = tabs.indexOfFirst { it.route == selectedRoute }.coerceAtLeast(0)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md)
            .padding(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + spacing.md,
            ),
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(barShape)
                .background(containerColor)
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            baseBorderColor,
                            baseBorderColor.copy(alpha = 0.25f),
                        ),
                    ),
                    shape = barShape,
                )
                .shadow(
                    elevation = tokens.bottomBarElevation.dp,
                    shape = barShape,
                    ambientColor = Color.Black.copy(
                        alpha = tokens.bottomBarAmbientShadowAlpha,
                    ),
                    spotColor = Color.Black.copy(
                        alpha = tokens.bottomBarSpotShadowAlpha,
                    ),
                )
                .padding(spacing.sm),
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val tabCount = tabs.size.coerceAtLeast(1)
                val tabWidth = maxWidth / tabCount

                val highlightWidth = tabWidth - (spacing.xs * 2)

                val highlightOffset by animateDpAsState(
                    targetValue = (tabWidth * selectedIndex) + spacing.xs,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow,
                    ),
                    label = "KsWalletBottomBarHighlightOffset",
                )

                Box(
                    modifier = Modifier
                        .offset(x = highlightOffset)
                        .width(highlightWidth)
                        .height(spacing.bottomBarItemHeight)
                        .clip(MaterialTheme.shapes.large)
                        .background(
                            color = colorScheme.primary.copy(
                                alpha = tokens.bottomBarSelectedBackgroundAlpha,
                            ),
                        ),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val selected = index == selectedIndex

                        KsWalletBottomBarItem(
                            tab = tab,
                            selected = selected,
                            showLabel = style == KsWalletBottomBarStyle.EXPANDED,
                            selectedColor = colorScheme.primary,
                            unselectedColor = colorScheme.onSurfaceVariant,
                            onClick = {
                                if (!selected) {
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.TextHandleMove,
                                    )
                                    onSelectRoute(tab.route)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(spacing.bottomBarItemHeight),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KsWalletBottomBarItem(
    tab: KsWalletTab,
    selected: Boolean,
    showLabel: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = KsWalletTheme.spacing
    val interactionSource = remember { MutableInteractionSource() }

    val iconTint by animateColorAsState(
        targetValue = if (selected) selectedColor else unselectedColor,
        animationSpec = tween(180),
        label = "KsWalletBottomBarItemIconColor",
    )

    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "KsWalletBottomBarItemIconScale",
    )

    val labelAlpha by animateFloatAsState(
        targetValue = if (!selected && showLabel) 1f else 0f,
        animationSpec = tween(160),
        label = "KsWalletBottomBarItemLabelAlpha",
    )

    val labelHeight by animateDpAsState(
        targetValue = if (!selected && showLabel) {
            MaterialTheme.typography.labelMedium.lineHeight.value.dp
        } else {
            0.dp
        },
        animationSpec = tween(180),
        label = "KsWalletBottomBarItemLabelHeight",
    )

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = if (selected) {
                    tab.selectedIcon
                } else {
                    tab.unselectedIcon
                },
                contentDescription = stringResource(tab.label),
                tint = iconTint,
                modifier = Modifier
                    .size(spacing.bottomBarIconSize)
                    .scale(iconScale),
            )

            Text(
                text = stringResource(tab.label),
                color = iconTint.copy(alpha = labelAlpha),
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .height(labelHeight)
                    .padding(top = spacing.xxs),
            )
        }
    }
}
