package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.SupportRequest
import com.example.vorratsheld.data.remote.CollectionNames
import com.example.vorratsheld.data.remote.EmailJsApi
import com.example.vorratsheld.data.remote.EmailJsConfig
import dev.gitlive.firebase.firestore.FirebaseFirestore

// Speichert die Anfrage in Firestore und schickt sie zusätzlich per Mail
class SupportRepositoryImpl(
    firestore: FirebaseFirestore,
    private val emailJsApi: EmailJsApi
) : SupportRepository {

    private val requestsCollection = firestore.collection(CollectionNames.SUPPORT_REQUESTS)

    override suspend fun send(request: SupportRequest): Result<Unit> = runCatching {
        // Mail ist nur ein Extra, auch wenn sie scheitert, geht die Anfrage nicht verloren
        val emailSent = EmailJsConfig.isConfigured && runCatching {
            emailJsApi.send(
                mapOf(
                    "category" to request.category.name,
                    "subject" to request.subject,
                    "message" to request.message,
                    "reply_to" to request.replyEmail,
                    "user_id" to request.userId,
                    "platform" to request.platform,
                    "app_version" to request.appVersion
                )
            )
        }.isSuccess

        requestsCollection.add(request.copy(emailSent = emailSent))
    }
}
