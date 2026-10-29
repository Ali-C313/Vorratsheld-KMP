package com.example.vorratsheld.ui.applock

import com.example.vorratsheld.data.enums.LockTimeout

// Zustand der App-Sperre für Einstellungen und Sperrbildschirm
data class AppLockUiState(
    val enabled: Boolean = false,
    val biometricsEnabled: Boolean = false,
    val timeout: LockTimeout = LockTimeout.ONE_MINUTE
)
