package com.example.vorratsheld.platform

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.core.content.ContextCompat
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.worker.WorkScheduler

@Composable
actual fun ExpiryReminderEffect(products: List<Product>) {
    // Preview: kein WorkManager und keine Berechtigungsabfrage
    if (LocalInspectionMode.current) return
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Ergebnis wird nicht extra behandelt: Ohne Erlaubnis zeigt WorkManager schlicht keine Notification */ }

    // Einmal pro Besuch der Übersicht: Check einplanen (KEEP verhindert doppelte Jobs) + Erlaubnis anfragen
    LaunchedEffect(Unit) {
        WorkScheduler.schedulePeriodicExpiryCheck(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val alreadyGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!alreadyGranted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
