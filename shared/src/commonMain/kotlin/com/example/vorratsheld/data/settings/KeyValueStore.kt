package com.example.vorratsheld.data.settings

// Kleiner lokaler Speicher für Einstellungen (Android: SharedPreferences, iOS: NSUserDefaults)
expect object KeyValueStore {
    fun getString(key: String): String?
    fun putString(key: String, value: String?)
}
