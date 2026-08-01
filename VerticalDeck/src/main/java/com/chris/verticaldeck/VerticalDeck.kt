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

package com.chris.verticaldeck

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
 * A custom layout that arranges its children in a vertical deck with overlapping cards.
 *
 * Each child overlaps the previous one by 50%. The layout automatically calculates
 * internal padding to allow the first and last items to reach the vertical center
 * of the viewport. It also features dynamic scaling and z-index adjustments based
 * on the scroll position, making the centered item appear larger and on top.
 *
 * @param scrollState The [androidx.compose.foundation.ScrollState] used to manage and observe the vertical scroll position.
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
fun VerticalDeck(
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
        val viewportHeightPx = constraints.maxHeight

        Box(
            modifier = Modifier
                .size(maxWidth, maxHeight)
                .verticalScroll(scrollState)
        ) {
            Layout(
                content = content
            ) { measurables, constraints ->
                val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
                val placeables = measurables.map { it.measure(childConstraints) }

                val firstCardHeight = placeables.firstOrNull()?.height ?: 0
                val lastCardHeight = placeables.lastOrNull()?.height ?: 0

                val startPadding = (viewportHeightPx - firstCardHeight) / 2f
                val endPadding = (viewportHeightPx - lastCardHeight) / 2f

                val tempSnapPoints = mutableListOf<Int>()
                var currentY = startPadding
                
                placeables.forEach { placeable ->
                    tempSnapPoints.add((currentY - (viewportHeightPx - placeable.height) / 2f).toInt())

                    currentY += (placeable.height * 0.5f).toInt()
                }

                if (snapPoints.size != tempSnapPoints.size || !snapPoints.indices.all { snapPoints[it] == tempSnapPoints[it] }) {
                    snapPoints.clear()
                    snapPoints.addAll(tempSnapPoints)
                }

                val totalHeight = (currentY + (lastCardHeight * 0.5f) + endPadding).toInt()
                    .coerceAtLeast(constraints.minHeight)

                val layoutWidth = (placeables.maxOfOrNull { it.width } ?: 0)
                    .coerceIn(constraints.minWidth, constraints.maxWidth)

                layout(layoutWidth, totalHeight) {
                    val scrollY = scrollState.value
                    val viewportCenterY = scrollY + viewportHeightPx / 2f

                    var yPosition = startPadding.toInt()
                    placeables.forEach { placeable ->
                        val itemCenterY = yPosition + placeable.height / 2f

                        val distanceFromCenter = abs(viewportCenterY - itemCenterY)

                        val maxDistance = viewportHeightPx / 2f
                        val fraction = (distanceFromCenter / maxDistance).coerceIn(0f, 1f)
                        val scale = 1f - (fraction * (1f - minScale))

                        placeable.placeRelativeWithLayer(x = 0, y = yPosition, zIndex = scale) {
                            scaleX = scale
                            scaleY = scale
                        }
                        yPosition += (placeable.height * 0.5f).toInt()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerticalDeckPreview() {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        VerticalDeck(
            scrollState = scrollState,
            modifier = Modifier.size(width = 200.dp, height = 400.dp)
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
