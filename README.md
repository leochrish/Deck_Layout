# Deck Layout (Horizontal & Vertical)

A customizable, overlapping card deck layout for Jetpack Compose with dynamic scaling, z-index management, and automatic scroll snapping.

## Features

- **Overlapping Layout**: Cards overlap each other by 50% for a compact, deck-like appearance.
- **Dynamic Scaling**: Cards scale smoothly from a customizable `minScale` (default 0.75f) at the screen edges to 1.0f at the horizontal/vertical center.
- **Dynamic Z-Index**: The card closest to the center is automatically brought to the front.
- **Automatic Snapping**: Smoothly glides the nearest card to the center of the screen when scrolling stops.
- **Center-Focus Scrolling**: Internally calculated padding ensures the first and last cards can reach the exact center of the viewport.
- **Item Selection Callback**: Receive the index of the centered card via the `onItemSelected` callback.
- **Apache 2.0 Licensed**: Fully protected under open-source guidelines.

## Screenshots

### Horizontal Deck
The `HorizontalDeck` provides a focused swiping experience where the center card "pops" into view.

<img width="350" height="264" alt="Screenshot 2026-08-01 at 2 41 39 PM" src="https://github.com/user-attachments/assets/ea9763a5-b77e-406b-ad2b-ae6d72f67838" />

### Vertical Deck
The `VerticalDeck` brings the same interactive experience to vertical lists, perfect for full-screen carousels.

<img width="352" height="403" alt="Screenshot 2026-08-01 at 2 42 15 PM" src="https://github.com/user-attachments/assets/7f2cb4f4-2bac-4fd4-a1b3-52b30b4da7fa" />

## Installation

### Horizontal Deck
```kotlin
implementation("io.github.leochrish:horizontal-deck:1.0.3")
```

### Vertical Deck
```kotlin
implementation("io.github.leochrish:vertical-deck:1.0.0")
```

## Usage

### Horizontal Deck
```kotlin
val scrollState = rememberScrollState()
HorizontalDeck(
    scrollState = scrollState,
    minScale = 0.65f,
    cardSelectionEnabled = true,
    onItemSelected = { index ->
        // Handle selection
    },
    modifier = Modifier.fillMaxWidth().height(260.dp)
) {
    // Add your card composables here
    MyCard()
    MyCard()
}
```

### Vertical Deck
```kotlin
val scrollState = rememberScrollState()
VerticalDeck(
    scrollState = scrollState,
    minScale = 0.65f,
    cardSelectionEnabled = true,
    onItemSelected = { index ->
        // Handle selection
    },
    modifier = Modifier.fillMaxWidth().weight(1f)
) {
    // Add your card composables here
    MyCard()
    MyCard()
}
```

## License
Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for more details.
