package com.example.vorratsheld.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import java.util.concurrent.TimeUnit

object WorkScheduler {

    private const val WORK_NAME = "expiry_check_worker"

    // Plant den täglichen Ablauf-Check ein. KEEP sorgt dafür, dass beim erneuten Aufruf
    // (z. B. bei jedem App-Start) keine doppelten Jobs entstehen.
    fun schedulePeriodicExpiryCheck(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<ExpiryCheckWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}