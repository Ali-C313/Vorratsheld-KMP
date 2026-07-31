package com.example.vorratsheld.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat

// Bündelt alles rund um den Notification-Channel und das Anzeigen der Ablauf-Erinnerung
object NotificationHelper {

    private const val CHANNEL_ID = "expiry_reminders"
    private const val NOTIFICATION_ID = 1001

    // Muss einmal beim App-Start aufgerufen werden (macht auf älteren Android-Versionen nichts)
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Ablauf-Erinnerungen",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Erinnert dich, wenn Produkte in deinem Vorrat bald ablaufen"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    // Zeigt eine Sammel-Notification für alle bald ablaufenden Produkte
    fun showExpiryNotification(context: Context, productNames: List<String>) {
        if (productNames.isEmpty()) return

        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return

        val title = if (productNames.size == 1) {
            "1 Produkt läuft bald ab"
        } else {
            "${productNames.size} Produkte laufen bald ab"
        }
        val text = productNames.joinToString(", ")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .build()

        androidx.core.app.NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID, notification)
    }
}