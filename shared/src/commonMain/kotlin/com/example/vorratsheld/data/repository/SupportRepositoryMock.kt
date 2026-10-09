package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.SupportRequest

// Für Previews: "Senden" klappt immer
class SupportRepositoryMock : SupportRepository {
    override suspend fun send(request: SupportRequest): Result<Unit> = Result.success(Unit)
}
