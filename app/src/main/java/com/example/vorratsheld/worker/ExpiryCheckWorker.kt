package com.example.vorratsheld.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.vorratsheld.data.enums.ExpiryUrgency
import com.example.vorratsheld.data.enums.expiryUrgencyFor
import com.example.vorratsheld.data.repository.ProductRepository
import com.example.vorratsheld.notification.NotificationHelper
import com.google.firebase.auth.FirebaseAuth

// Läuft täglich im Hintergrund (siehe WorkScheduler) und prüft, ob Produkte bald ablaufen
class ExpiryCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val productRepository = ProductRepository()

    override suspend fun doWork(): Result {
        // Ohne eingeloggten Nutzer gibt es nichts zu prüfen (z. B. nach Logout)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.success()

        return try {
            val products = productRepository.getProducts(userId)
            val soonExpiring = products.filter { product ->
                val urgency = expiryUrgencyFor(product.expiryDate)
                urgency == ExpiryUrgency.BALD_ABLAUFEND || urgency == ExpiryUrgency.ABGELAUFEN
            }

            NotificationHelper.showExpiryNotification(
                context = applicationContext,
                productNames = soonExpiring.map { it.name }
            )

            Result.success()
        } catch (e: Exception) {
            // Kurzer Netzwerkfehler o.ä. -> WorkManager versucht es beim nächsten Mal erneut
            Result.retry()
        }
    }
}