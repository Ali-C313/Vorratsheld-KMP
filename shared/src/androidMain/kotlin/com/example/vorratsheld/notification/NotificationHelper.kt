package com.example.vorratsheld.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.notification_channel_description
import com.example.vorratsheld.resources.notification_channel_name
import com.example.vorratsheld.resources.notification_many_expiring
import com.example.vorratsheld.resources.notification_one_expiring
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

// Bündelt alles rund um den Notification-Channel und das Anzeigen der Ablauf-Erinnerung
object NotificationHelper {

    private const val CHANNEL_ID = "expiry_reminders"
    private const val NOTIFICATION_ID = 1001

    // Einmal beim App-Start aufrufen (macht auf älteren Android-Versionen nichts)
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        // Texte kommen aus den Ressourcen (suspend), deshalb im Hintergrund
        CoroutineScope(Dispatchers.Default).launch {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(Res.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(Res.string.notification_channel_description)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    // Zeigt eine Sammel-Notification für alle bald ablaufenden Produkte
    suspend fun showExpiryNotification(context: Context, productNames: List<String>) {
        if (productNames.isEmpty()) return

        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return

        val title = if (productNames.size == 1) {
            getString(Res.string.notification_one_expiring)
        } else {
            getString(Res.string.notification_many_expiring, productNames.size)
        }
        val text = productNames.joinToString(", ")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
