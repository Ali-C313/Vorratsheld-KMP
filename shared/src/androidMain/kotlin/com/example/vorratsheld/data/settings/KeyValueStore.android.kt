package com.example.vorratsheld.data.settings

import android.content.Context
import android.content.SharedPreferences

actual object KeyValueStore {
    private var prefs: SharedPreferences? = null

    // Einmal in VorratsheldApp.onCreate aufrufen
    fun init(context: Context) {
        prefs = context.getSharedPreferences("vorratsheld_settings", Context.MODE_PRIVATE)
    }

    actual fun getString(key: String): String? = prefs?.getString(key, null)

    actual fun putString(key: String, value: String?) {
        prefs?.edit()?.apply {
            if (value == null) remove(key) else putString(key, value)
            apply()
        }
    }
}
