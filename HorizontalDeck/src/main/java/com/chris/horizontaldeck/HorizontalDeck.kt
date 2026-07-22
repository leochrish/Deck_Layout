package com.chris.horizontaldeck

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun HorizontalDeck(
    scrollState: ScrollState,
    viewportWidth: Int,
    modifier: Modifier = Modifier,
    minScale: Float = 0.75f,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        // Measure each child with the incoming constraints
        val placeables = measurables.map { it.measure(constraints) }

        // Calculate total width with 50% overlap
        var layoutWidth = 0
        placeables.forEachIndexed { index, placeable ->
            if (index == 0) {
                layoutWidth += placeable.width
            } else {
                layoutWidth += (placeable.width * 0.5f).toInt()
            }
        }
        layoutWidth = layoutWidth.coerceIn(constraints.minWidth, constraints.maxWidth)

        // Calculate layout height as the maximum height of any child
        val layoutHeight = (placeables.maxOfOrNull { it.height } ?: 0)
            .coerceIn(constraints.minHeight, constraints.maxHeight)

        layout(layoutWidth, layoutHeight) {
            val scrollX = scrollState.value
            val viewportCenterX = scrollX + viewportWidth / 2f

            var xPosition = 0
            placeables.forEach { placeable ->
                // Calculate the center of the item relative to the layout's content
                val itemCenterX = xPosition + placeable.width / 2f
                
                // Calculate distance from viewport center
                val distanceFromCenter = abs(viewportCenterX - itemCenterX)
                
                // Normalize distance to a scale factor (1.0 at center, minScale at edges)
                // We use half the viewport width as the max distance for scaling effect
                val maxDistance = viewportWidth / 2f
                val fraction = (distanceFromCenter / maxDistance).coerceIn(0f, 1f)
                val scale = 1f - (fraction * (1f - minScale))

                placeable.placeRelativeWithLayer(x = xPosition, y = 0, zIndex = scale) {
                    scaleX = scale
                    scaleY = scale
                }
                // Next item starts 50% into the current item
                xPosition += (placeable.width * 0.5f).toInt()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HorizontalDeckPreview() {
    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        HorizontalDeck(
            scrollState = scrollState,
            viewportWidth = 1000,
            modifier = Modifier.size(width = 400.dp, height = 200.dp)
        ) {
            Box(modifier = Modifier.size(100.dp).background(Color.Red))
            Box(modifier = Modifier.size(100.dp).background(Color.Green))
            Box(modifier = Modifier.size(100.dp).background(Color.Blue))
            Box(modifier = Modifier.size(100.dp).background(Color.Yellow))
        }
    }
}
