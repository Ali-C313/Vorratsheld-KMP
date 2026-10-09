package com.example.vorratsheld.data.enums

// Verfügbare Sprachen, Name jeweils in der eigenen Sprache (so findet man seine immer)
enum class AppLanguage(val code: String, val nativeName: String) {
    DEUTSCH("de", "Deutsch"),
    ENGLISH("en", "English"),
    TUERKCE("tr", "Türkçe"),
    ARABIC("ar", "العربية"),
    FRANCAIS("fr", "Français"),
    ESPANOL("es", "Español"),
    ITALIANO("it", "Italiano"),
    POLSKI("pl", "Polski"),
    RUSSIAN("ru", "Русский"),
    UKRAINIAN("uk", "Українська")
}
