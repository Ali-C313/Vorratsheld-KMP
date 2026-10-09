package com.example.vorratsheld.ui.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

// Vom Nutzer gewählte Sprache (z. B. "en"), null = Systemsprache
var customAppLocale by mutableStateOf<String?>(null)

// Setzt die Sprache auf der jeweiligen Plattform um (Android: Configuration, iOS: AppleLanguages)
expect object LocalAppLocale {
    val current: String @Composable get

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}

// Um die ganze App legen: Sprachwechsel baut alles neu auf, Arabisch läuft von rechts nach links
@Composable
fun AppEnvironment(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppLocale provides customAppLocale) {
        val isRtl = LocalAppLocale.current.startsWith("ar")
        CompositionLocalProvider(
            LocalLayoutDirection provides if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            key(customAppLocale) {
                content()
            }
        }
    }
}
