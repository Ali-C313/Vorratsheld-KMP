package com.example.vorratsheld.ui.state

import com.example.vorratsheld.data.enums.SupportCategory

// Zustand des Hilfe-Formulars
data class SupportUiState(
    val category: SupportCategory = SupportCategory.PROBLEM,
    val subject: String = "",
    val message: String = "",
    val replyEmail: String = "",
    val isSending: Boolean = false,
    val isSent: Boolean = false,
    val errorMessage: String? = null
)
