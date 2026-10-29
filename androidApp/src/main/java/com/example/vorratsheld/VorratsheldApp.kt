package com.example.vorratsheld

import android.app.Application
import com.example.vorratsheld.data.settings.KeyValueStore
import com.example.vorratsheld.di.initKoin
import com.example.vorratsheld.notification.NotificationHelper
import com.example.vorratsheld.ui.locale.customAppLocale
import java.util.Locale

// Läuft vor allem anderen, auch vor dem WorkManager-Worker, der Koin braucht
class VorratsheldApp : Application() {
    override fun onCreate() {
        super.onCreate()
        KeyValueStore.init(this)
        initKoin()

        // Gewählte Sprache auch für Benachrichtigungen im Hintergrund nutzen
        customAppLocale?.let { Locale.setDefault(Locale.forLanguageTag(it)) }

        NotificationHelper.createNotificationChannel(this)
    }
}
