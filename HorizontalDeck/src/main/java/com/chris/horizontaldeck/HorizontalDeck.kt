/*
 * Copyright 2024 Leoni Christopher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chris.horizontaldeck

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.abs


/**
 * A custom layout that arranges its children in a horizontal deck with overlapping cards.
 *
 * Each child overlaps the previous one by 50%. The layout automatically calculates
 * internal padding to allow the first and last items to reach the horizontal center
 * of the viewport. It also features dynamic scaling and z-index adjustments based
 * on the scroll position, making the centered item appear larger and on top.
 *
 * @param scrollState The [ScrollState] used to manage and observe the horizontal scroll position.
 * @param modifier The modifier to be applied to the layout's viewport. This determines
 * the size of the deck's visible area.
 * @param minScale The minimum scale factor (between 0f and 1f) applied to items at the edges
 * of the viewport. As items move towards the center, they scale up to 1.0f. Defaults to 0.75f.
 * @param cardSelectionEnabled A Flag that controls the single card selection behavior. If this is
 * true then only the [onItemSelected] will be invoked.
 * @param onItemSelected A callback triggered when a card becomes centered after scrolling stops.
 * Receives the index of the selected (centered) item.
 * @param content The composable content representing the cards in the deck.
 */
@Composable
fun HorizontalDeck(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    minScale: Float = 0.75f,
    cardSelectionEnabled: Boolean = false,
    onItemSelected: (Int) -> Unit = {},
    content: @Composable () -> Unit
) {
    val snapPoints = remember { mutableStateListOf<Int>() }
    var lastSelectedIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(scrollState.isScrollInProgress) {
        if (!scrollState.isScrollInProgress && snapPoints.isNotEmpty() && cardSelectionEnabled) {
            val currentScroll = scrollState.value
            val closestSnapPoint = snapPoints.minByOrNull { abs(it - currentScroll) }
            val index = snapPoints.indexOf(closestSnapPoint)

            if (closestSnapPoint != null && closestSnapPoint != currentScroll) {
                scrollState.animateScrollTo(closestSnapPoint)
            }

            if (index != -1 && index != lastSelectedIndex) {
                lastSelectedIndex = index
                onItemSelected(index)
            }
        }
    }

    BoxWithConstraints(modifier = modifier) {
        val viewportWidthPx = constraints.maxWidth

        Box(
            modifier = Modifier
                .size(maxWidth, maxHeight)
                .horizontalScroll(scrollState)
        ) {
            Layout(
                content = content
            ) { measurables, constraints ->
                val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
                val placeables = measurables.map { it.measure(childConstraints) }

                val firstCardWidth = placeables.firstOrNull()?.width ?: 0
                val lastCardWidth = placeables.lastOrNull()?.width ?: 0

                val startPadding = (viewportWidthPx - firstCardWidth) / 2f
                val endPadding = (viewportWidthPx - lastCardWidth) / 2f

                val tempSnapPoints = mutableListOf<Int>()
                var currentX = startPadding
                
                placeables.forEach { placeable ->
                    tempSnapPoints.add((currentX - (viewportWidthPx - placeable.width) / 2f).toInt())

                    currentX += (placeable.width * 0.5f).toInt()
                }

                if (snapPoints.size != tempSnapPoints.size || !snapPoints.indices.all { snapPoints[it] == tempSnapPoints[it] }) {
                    snapPoints.clear()
                    snapPoints.addAll(tempSnapPoints)
                }

                val totalWidth = (currentX + (lastCardWidth * 0.5f) + endPadding).toInt()
                    .coerceAtLeast(constraints.minWidth)

                val layoutHeight = (placeables.maxOfOrNull { it.height } ?: 0)
                    .coerceIn(constraints.minHeight, constraints.maxHeight)

                layout(totalWidth, layoutHeight) {
                    val scrollX = scrollState.value
                    val viewportCenterX = scrollX + viewportWidthPx / 2f

                    var xPosition = startPadding.toInt()
                    placeables.forEach { placeable ->
                        val itemCenterX = xPosition + placeable.width / 2f

                        val distanceFromCenter = abs(viewportCenterX - itemCenterX)

                        val maxDistance = viewportWidthPx / 2f
                        val fraction = (distanceFromCenter / maxDistance).coerceIn(0f, 1f)
                        val scale = 1f - (fraction * (1f - minScale))

                        placeable.placeRelativeWithLayer(x = xPosition, y = 0, zIndex = scale) {
                            scaleX = scale
                            scaleY = scale
                        }
                        xPosition += (placeable.width * 0.5f).toInt()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HorizontalDeckPreview() {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HorizontalDeck(
            scrollState = scrollState,
            modifier = Modifier.size(width = 400.dp, height = 200.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Red)
            )
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Green)
            )
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Blue)
            )
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Yellow)
            )
        }
    }
}
