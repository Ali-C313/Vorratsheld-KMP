package com.example.vorratsheld.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Kamera-Vorschau mit Texterkennung, liefert laufend den erkannten Text
@Composable
expect fun ExpiryTextScanner(
    onTextRecognized: (String) -> Unit,
    modifier: Modifier = Modifier,
    torchEnabled: Boolean = false
)
