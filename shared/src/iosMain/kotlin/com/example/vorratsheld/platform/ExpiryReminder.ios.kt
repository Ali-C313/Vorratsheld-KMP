package com.example.vorratsheld.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.reminder_body
import com.example.vorratsheld.resources.reminder_title
import com.example.vorratsheld.util.DateHelper
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.number
import org.jetbrains.compose.resources.getString
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter

// iOS hat keinen WorkManager: Stattdessen wird pro Produkt eine lokale Benachrichtigung
// für 9 Uhr, 3 Tage vor Ablauf eingeplant. Läuft bei jeder Änderung der Liste neu.
@Composable
actual fun ExpiryReminderEffect(products: List<Product>) {
    LaunchedEffect(products) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        // Texte vorher holen, der Callback läuft außerhalb von Compose
        val title = getString(Res.string.reminder_title)
        val bodies = products.associate { it.id to getString(Res.string.reminder_body, it.name) }

        center.requestAuthorizationWithOptions(UNAuthorizationOptionAlert or UNAuthorizationOptionSound) { granted, _ ->
            if (granted) {
                scheduleReminders(center, products, title, bodies)
            }
        }
    }
}

private fun scheduleReminders(
    center: UNUserNotificationCenter,
    products: List<Product>,
    title: String,
    bodies: Map<String, String>
) {
    // Alte Erinnerungen löschen, damit gelöschte oder geänderte Produkte nicht mehr auftauchen
    center.removeAllPendingNotificationRequests()

    val today = DateHelper.today()
    products.forEach { product ->
        val reminderDay = DateHelper.expiryDateToLocalDate(product.expiryDate).minus(3, DateTimeUnit.DAY)
        // Schon innerhalb der 3 Tage, das sieht man in der App
        if (reminderDay <= today) return@forEach

        // title/body sind in Kotlin nur lesbar (kommen aus UNNotificationContent), daher die Setter
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(bodies[product.id] ?: product.name)
        }
        val dateComponents = NSDateComponents().apply {
            year = reminderDay.year.toLong()
            month = reminderDay.month.number.toLong()
            day = reminderDay.day.toLong()
            hour = 9L
        }
        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(dateComponents, repeats = false)
        val request = UNNotificationRequest.requestWithIdentifier(product.id, content, trigger)
        center.addNotificationRequest(request, withCompletionHandler = null)
    }
}
