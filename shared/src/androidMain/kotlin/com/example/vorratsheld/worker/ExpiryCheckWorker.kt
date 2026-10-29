package com.example.vorratsheld.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.vorratsheld.data.enums.ExpiryUrgency
import com.example.vorratsheld.data.repository.AuthRepository
import com.example.vorratsheld.data.repository.ProductRepository
import com.example.vorratsheld.notification.NotificationHelper
import com.example.vorratsheld.util.DateHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// Läuft täglich im Hintergrund (siehe WorkScheduler) und prüft, ob Produkte bald ablaufen
class ExpiryCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    // Repositories kommen aus Koin, weil WorkManager den Worker selbst erzeugt
    private val productRepository: ProductRepository by inject()
    private val authRepository: AuthRepository by inject()

    override suspend fun doWork(): Result {
        // Ohne eingeloggten Nutzer gibt es nichts zu prüfen (z. B. nach Logout)
        val userId = authRepository.getCurrentUser()?.uid ?: return Result.success()

        return try {
            val products = productRepository.getProducts(userId).getOrThrow()
            val soonExpiring = products.filter { product ->
                val urgency = DateHelper.expiryUrgencyFor(product.expiryDate)
                urgency == ExpiryUrgency.BALD_ABLAUFEND || urgency == ExpiryUrgency.ABGELAUFEN
            }

            NotificationHelper.showExpiryNotification(
                context = applicationContext,
                productNames = soonExpiring.map { it.name }
            )

            Result.success()
        } catch (e: Exception) {
            // Bei Netzwerkfehlern versucht es WorkManager später noch mal
            Result.retry()
        }
    }
}
