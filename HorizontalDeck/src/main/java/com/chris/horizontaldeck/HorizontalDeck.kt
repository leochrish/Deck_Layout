package com.chris.horizontaldeck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalDeck(
    modifier: Modifier = Modifier,
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
                // Subsequent children overlap 50% of the previous child's width
                // Assuming uniform width for simplicity in calculation, 
                // but this works for varying widths too by shifting 50% of current child
                layoutWidth += (placeable.width * 0.5f).toInt()
            }
        }
        layoutWidth = layoutWidth.coerceIn(constraints.minWidth, constraints.maxWidth)

        // Calculate layout height as the maximum height of any child
        val layoutHeight = (placeables.maxOfOrNull { it.height } ?: 0)
            .coerceIn(constraints.minHeight, constraints.maxHeight)

        layout(layoutWidth, layoutHeight) {
            var xPosition = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(x = xPosition, y = 0)
                // Next item starts 50% into the current item
                xPosition += (placeable.width * 0.5f).toInt()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HorizontalDeckPreview() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        HorizontalDeck(modifier = Modifier.size(width = 400.dp, height = 200.dp)) {
            Box(modifier = Modifier.size(100.dp).background(Color.Red))
            Box(modifier = Modifier.size(100.dp).background(Color.Green))
            Box(modifier = Modifier.size(100.dp).background(Color.Blue))
            Box(modifier = Modifier.size(100.dp).background(Color.Yellow))
        }
    }
}
