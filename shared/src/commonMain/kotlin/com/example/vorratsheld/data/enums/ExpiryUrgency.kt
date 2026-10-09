package com.example.vorratsheld.data.enums

// Dringlichkeitsstufe, abgeleitet aus dem Ablaufdatum eines Produkts
enum class ExpiryUrgency {
    ABGELAUFEN,
    BALD_ABLAUFEND,   // <= 3 Tage
    IN_KUERZE,        // <= 10 Tage
    UNKRITISCH
}
