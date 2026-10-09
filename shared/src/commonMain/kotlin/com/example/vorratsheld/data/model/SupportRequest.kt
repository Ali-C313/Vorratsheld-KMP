package com.example.vorratsheld.data.model

import com.example.vorratsheld.data.enums.SupportCategory
import kotlinx.serialization.Serializable

// Anfrage aus dem Hilfe-Formular, landet in Firestore unter supportRequests
@Serializable
data class SupportRequest(
    val userId: String = "",
    val replyEmail: String = "",
    val category: SupportCategory = SupportCategory.QUESTION,
    val subject: String = "",
    val message: String = "",
    val platform: String = "",
    val appVersion: String = "",
    val createdAt: Long = 0L,
    val emailSent: Boolean = false
)
