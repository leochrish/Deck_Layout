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

package com.chris.decklayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chris.decklayout.ui.theme.DeckLayoutTheme
import com.chris.horizontaldeck.HorizontalDeck
import com.chris.verticaldeck.VerticalDeck

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeckLayoutTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val context = LocalContext.current
                    Column(modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()) {
                        Text(
                            text = "Horizontal Deck",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(16.dp)
                        )
                        val horizontalScrollState = rememberScrollState()
                        HorizontalDeck(
                            scrollState = horizontalScrollState,
                            minScale = 0.65f,
                            cardSelectionEnabled = true,
                            onItemSelected = { index ->
                                Toast.makeText(
                                    context,
                                    "Horizontal Selected: ${index + 1}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        ) {
                            SampleCardsHorizontal()
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Vertical Deck",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(16.dp)
                        )
                        val verticalScrollState = rememberScrollState()
                        VerticalDeck(
                            scrollState = verticalScrollState,
                            minScale = 0.65f,
                            cardSelectionEnabled = true,
                            onItemSelected = { index: Int ->
                                Toast.makeText(
                                    context,
                                    "Vertical Selected: ${index + 1}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            SampleCardsVertical()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SampleCardsHorizontal() {
    val colors = listOf(
        Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7),
        Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF03A9F4),
        Color(0xFF00BCD4), Color(0xFF009688), Color(0xFF4CAF50),
        Color(0xFF8BC34A), Color(0xFFCDDC39), Color(0xFFFFEB3B)
    )
    colors.forEachIndexed { index, color ->
        DeckCardHorizontal(
            title = "Card ${index + 1}",
            description = "Description for card ${index + 1}.",
            backgroundColor = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SampleCardsHorizontalPreview() {
    SampleCardsHorizontal()
}

@Composable
fun SampleCardsVertical() {
    val colors = listOf(
        Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7),
        Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF03A9F4),
        Color(0xFF00BCD4), Color(0xFF009688), Color(0xFF4CAF50),
        Color(0xFF8BC34A), Color(0xFFCDDC39), Color(0xFFFFEB3B)
    )
    colors.forEachIndexed { index, color ->
        DeckCardVertical(
            title = "Card ${index + 1}",
            description = "Description for card ${index + 1}.",
            backgroundColor = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SampleCardsVerticalPreview() {
    SampleCardsVertical()
}

@Composable
fun DeckCardVertical(title: String, description: String, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun DeckCardHorizontal(title: String, description: String, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .size(width = 160.dp, height = 240.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DeckLayoutTheme {
        Greeting("Android")
    }
}
