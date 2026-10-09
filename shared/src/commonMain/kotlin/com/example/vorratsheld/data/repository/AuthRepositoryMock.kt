package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.User

// Für Previews: Testnutzer, für Login/Registrierung mit loggedIn = false
class AuthRepositoryMock(loggedIn: Boolean = true) : AuthRepository {

    private var currentUser: User? = if (loggedIn) {
        User(uid = MOCK_USER_ID, email = "ali@example.com", displayName = "Ali Chehab")
    } else {
        null
    }

    override fun getCurrentUser(): User? = currentUser

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        User(uid = MOCK_USER_ID, email = email).also { currentUser = it }
    }

    override suspend fun register(email: String, password: String, displayName: String): Result<User> = runCatching {
        User(uid = MOCK_USER_ID, email = email, displayName = displayName).also { currentUser = it }
    }

    override suspend fun updateDisplayName(displayName: String): Result<Unit> = runCatching {
        currentUser = currentUser?.copy(displayName = displayName)
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> =
        Result.success(Unit)

    override suspend fun changeEmail(currentPassword: String, newEmail: String): Result<Unit> =
        Result.success(Unit)

    override suspend fun confirmPassword(currentPassword: String): Result<Unit> = Result.success(Unit)

    override suspend fun deleteUser(): Result<Unit> = runCatching { currentUser = null }

    override suspend fun logout(): Result<Unit> = runCatching { currentUser = null }

    companion object {
        const val MOCK_USER_ID = "mock-user"
    }
}
