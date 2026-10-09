package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.UserProfile

// Profildaten (Name, Foto) des Nutzers
interface UserRepository {
    // Leeres Profil, wenn noch nichts gespeichert wurde
    suspend fun getProfile(userId: String): Result<UserProfile>

    suspend fun saveProfile(userId: String, profile: UserProfile): Result<Unit>

    suspend fun deleteProfile(userId: String): Result<Unit>
}
