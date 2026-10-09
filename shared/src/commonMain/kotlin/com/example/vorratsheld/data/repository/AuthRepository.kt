package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.User

// Anmeldung und Konto – umgesetzt mit Firebase Auth (Impl) bzw. Testdaten (Mock)
interface AuthRepository {
    // null, wenn niemand eingeloggt ist
    fun getCurrentUser(): User?

    suspend fun login(email: String, password: String): Result<User>

    // displayName wird direkt mitgesetzt, damit der Name auch ohne Firestore da ist
    suspend fun register(email: String, password: String, displayName: String): Result<User>

    suspend fun updateDisplayName(displayName: String): Result<Unit>

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>

    // Neue Adresse gilt erst, wenn der Link in der Bestätigungsmail angeklickt wurde
    suspend fun changeEmail(currentPassword: String, newEmail: String): Result<Unit>

    // Prüft das Passwort, bevor Daten gelöscht werden
    suspend fun confirmPassword(currentPassword: String): Result<Unit>

    suspend fun deleteUser(): Result<Unit>

    suspend fun logout(): Result<Unit>
}
