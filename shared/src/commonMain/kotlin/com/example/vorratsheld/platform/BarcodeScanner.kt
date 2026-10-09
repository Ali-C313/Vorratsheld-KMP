package com.example.vorratsheld.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Kamera-Vorschau mit Barcode-Erkennung inkl. Berechtigungsabfrage.
// onBarcodeDetected wird nur einmal pro Scan aufgerufen.
@Composable
expect fun BarcodeScanner(
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier = Modifier,
    torchEnabled: Boolean = false
)
