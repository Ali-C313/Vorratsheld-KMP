package com.example.vorratsheld.data.settings

// Einstellungen, die nur auf dem Gerät gespeichert werden
object AppSettings {
    private const val KEY_LANGUAGE = "app_language"
    private const val KEY_ONBOARDING_DONE = "onboarding_done"

    // null = Systemsprache
    var language: String?
        get() = KeyValueStore.getString(KEY_LANGUAGE)
        set(value) = KeyValueStore.putString(KEY_LANGUAGE, value)

    // Onboarding nur beim allerersten Start zeigen
    var onboardingDone: Boolean
        get() = KeyValueStore.getString(KEY_ONBOARDING_DONE) == "true"
        set(value) = KeyValueStore.putString(KEY_ONBOARDING_DONE, value.toString())
}
