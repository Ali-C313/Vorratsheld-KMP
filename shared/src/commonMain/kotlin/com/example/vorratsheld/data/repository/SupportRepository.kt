package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.SupportRequest

// Anfragen aus dem Hilfe-Formular
interface SupportRepository {
    suspend fun send(request: SupportRequest): Result<Unit>
}
