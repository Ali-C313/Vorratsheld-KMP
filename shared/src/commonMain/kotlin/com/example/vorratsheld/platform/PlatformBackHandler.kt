package com.example.vorratsheld.platform

import androidx.compose.runtime.Composable

// System-Zurück abfangen (nur Android, iOS hat keine Zurück-Taste)
@Composable
expect fun PlatformBackHandler(enabled: Boolean = true, onBack: () -> Unit)
