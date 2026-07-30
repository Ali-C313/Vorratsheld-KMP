package com.example.vorratsheld.data.enums

import java.util.concurrent.TimeUnit

// Dringlichkeitsstufe, abgeleitet aus dem Ablaufdatum eines Produkts
enum class ExpiryUrgency {
    ABGELAUFEN,
    BALD_ABLAUFEND,   // <= 3 Tage
    IN_KUERZE,        // <= 10 Tage
    UNKRITISCH
}

// Berechnet, wie viele Tage bis zum Ablaufdatum verbleiben (kann negativ sein, wenn abgelaufen)
fun daysUntilExpiry(expiryDate: Long, now: Long = System.currentTimeMillis()): Long {
    return TimeUnit.MILLISECONDS.toDays(expiryDate - now)
}

fun expiryUrgencyFor(expiryDate: Long, now: Long = System.currentTimeMillis()): ExpiryUrgency {
    val days = daysUntilExpiry(expiryDate, now)
    return when {
        days < 0 -> ExpiryUrgency.ABGELAUFEN
        days <= 3 -> ExpiryUrgency.BALD_ABLAUFEND
        days <= 10 -> ExpiryUrgency.IN_KUERZE
        else -> ExpiryUrgency.UNKRITISCH
    }
}