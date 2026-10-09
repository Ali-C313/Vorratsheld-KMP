package com.example.vorratsheld.platform

import androidx.compose.runtime.Composable
import com.example.vorratsheld.data.enums.BiometricResult
import com.example.vorratsheld.data.enums.BiometricType

// Face ID / Touch ID auf iOS, Fingerabdruck oder Gesicht auf Android
interface BiometricAuthenticator {
    // NONE, wenn nichts eingerichtet ist
    val type: BiometricType

    // fallbackLabel: Text für den "PIN verwenden"-Button im System-Dialog
    suspend fun authenticate(reason: String, fallbackLabel: String): BiometricResult
}

@Composable
expect fun rememberBiometricAuthenticator(): BiometricAuthenticator
