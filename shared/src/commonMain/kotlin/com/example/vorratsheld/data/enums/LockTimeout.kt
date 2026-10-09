package com.example.vorratsheld.data.enums

// Wann die App nach dem Verlassen wieder gesperrt wird
enum class LockTimeout(val seconds: Int) {
    IMMEDIATELY(0),
    ONE_MINUTE(60),
    FIVE_MINUTES(300),
    ON_START_ONLY(-1)
}
