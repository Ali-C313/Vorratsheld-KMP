package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.User
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.error_login_failed
import com.example.vorratsheld.resources.error_no_email
import com.example.vorratsheld.resources.error_not_logged_in
import com.example.vorratsheld.resources.error_register_failed
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth
import org.jetbrains.compose.resources.getString

// Anmeldung und Konto über Firebase Auth
class AuthRepositoryImpl(
    private val auth: FirebaseAuth
) : AuthRepository {

    override fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName ?: ""
        )
    }

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        val firebaseUser = auth.signInWithEmailAndPassword(email, password).user
            ?: throw Exception(getString(Res.string.error_login_failed))
        User(uid = firebaseUser.uid, email = firebaseUser.email ?: "")
    }

    override suspend fun register(email: String, password: String, displayName: String): Result<User> = runCatching {
        val firebaseUser = auth.createUserWithEmailAndPassword(email, password).user
            ?: throw Exception(getString(Res.string.error_register_failed))
        firebaseUser.updateProfile(displayName = displayName)
        User(uid = firebaseUser.uid, email = firebaseUser.email ?: "", displayName = displayName)
    }

    override suspend fun updateDisplayName(displayName: String): Result<Unit> = runCatching {
        auth.currentUser?.updateProfile(displayName = displayName)
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> = runCatching {
        reauthenticate(currentPassword)
        auth.currentUser?.updatePassword(newPassword)
    }

    override suspend fun changeEmail(currentPassword: String, newEmail: String): Result<Unit> = runCatching {
        reauthenticate(currentPassword)
        auth.currentUser?.verifyBeforeUpdateEmail(newEmail)
    }

    override suspend fun confirmPassword(currentPassword: String): Result<Unit> = runCatching {
        reauthenticate(currentPassword)
    }

    override suspend fun deleteUser(): Result<Unit> = runCatching {
        auth.currentUser?.delete()
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        auth.signOut()
    }

    // Firebase verlangt vor Passwort-, E-Mail-Änderung und Löschen eine frische Anmeldung
    private suspend fun reauthenticate(currentPassword: String) {
        val firebaseUser = auth.currentUser ?: throw Exception(getString(Res.string.error_not_logged_in))
        val email = firebaseUser.email ?: throw Exception(getString(Res.string.error_no_email))
        firebaseUser.reauthenticate(EmailAuthProvider.credential(email, currentPassword))
    }
}
