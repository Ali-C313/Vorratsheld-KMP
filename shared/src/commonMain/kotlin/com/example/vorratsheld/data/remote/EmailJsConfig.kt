package com.example.vorratsheld.data.remote

// Zugangsdaten von emailjs.com (kostenlos bis 200 Mails im Monat)
object EmailJsConfig {
    const val SERVICE_ID = "DEINE_SERVICE_ID"
    const val TEMPLATE_ID = "DEINE_TEMPLATE_ID"
    const val PUBLIC_KEY = "DEIN_PUBLIC_KEY"
    const val PRIVATE_KEY = "DEIN_PRIVATE_KEY"

    // Solange die Platzhalter drinstehen, wird nur in Firestore gespeichert
    val isConfigured: Boolean get() = !SERVICE_ID.startsWith("DEINE")
}
