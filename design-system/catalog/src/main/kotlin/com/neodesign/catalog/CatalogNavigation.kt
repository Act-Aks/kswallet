package com.neodesign.catalog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.neodesign.component.NeoNavigationItem

internal enum class CatalogDestination {
    Foundations,
    Components,
    Examples,
}

internal fun catalogNavigationItems(): List<NeoNavigationItem<CatalogDestination>> =
    CatalogDestination.entries.map { destination ->
        NeoNavigationItem(
            key = destination,
            label = destination.name,
            icon = { selected, tint ->
                CatalogNavigationIcon(
                    destination = destination,
                    selected = selected,
                    tint = tint,
                )
            },
        )
    }

@Composable
private fun CatalogNavigationIcon(
    destination: CatalogDestination,
    selected: Boolean,
    tint: Color,
) {
    Canvas(
        modifier = Modifier.fillMaxSize(),
    ) {
        val strokeWidth = if (selected) {
            2.2.dp.toPx()
        } else {
            1.6.dp.toPx()
        }

        when (destination) {
            CatalogDestination.Foundations -> {
                drawCircle(
                    color = tint,
                    radius = size.minDimension * 0.34f,
                    center = center,
                    style = Stroke(strokeWidth),
                )

                drawCircle(
                    color = tint,
                    radius = size.minDimension * 0.1f,
                    center = center,
                )
            }

            CatalogDestination.Components -> {
                val cellSize = Size(
                    width = size.width * 0.28f,
                    height = size.height * 0.28f,
                )

                listOf(
                    Offset(size.width * 0.12f, size.height * 0.12f),
                    Offset(size.width * 0.60f, size.height * 0.12f),
                    Offset(size.width * 0.12f, size.height * 0.60f),
                    Offset(size.width * 0.60f, size.height * 0.60f),
                ).forEach { position ->
                    drawRect(
                        color = tint,
                        topLeft = position,
                        size = cellSize,
                        style = Stroke(strokeWidth),
                    )
                }
            }

            CatalogDestination.Examples -> {
                val path = Path().apply {
                    moveTo(size.width * 0.30f, size.height * 0.18f)
                    lineTo(size.width * 0.80f, size.height * 0.50f)
                    lineTo(size.width * 0.30f, size.height * 0.82f)
                    close()
                }

                drawPath(
                    path = path,
                    color = tint,
                    style = Stroke(strokeWidth),
                )
            }
        }
    }
}