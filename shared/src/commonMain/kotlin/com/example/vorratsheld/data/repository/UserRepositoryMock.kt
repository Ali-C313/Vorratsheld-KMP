package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.UserProfile

// Für Previews: Profil ohne Foto
class UserRepositoryMock : UserRepository {

    private var profile = UserProfile(firstName = "Ali", lastName = "Chehab")

    override suspend fun getProfile(userId: String): Result<UserProfile> = Result.success(profile)

    override suspend fun saveProfile(userId: String, profile: UserProfile): Result<Unit> = runCatching {
        this.profile = profile
    }

    override suspend fun deleteProfile(userId: String): Result<Unit> = runCatching {
        profile = UserProfile()
    }
}
