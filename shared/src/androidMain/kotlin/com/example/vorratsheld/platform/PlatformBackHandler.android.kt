package com.example.vorratsheld.platform

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // In der Preview gibt es keinen Back-Dispatcher
    if (LocalInspectionMode.current) return
    BackHandler(enabled = enabled, onBack = onBack)
}
