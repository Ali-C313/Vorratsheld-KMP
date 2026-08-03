package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

// Kapselt den Zugriff auf Firebase Auth
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    // Aktuell eingeloggter Nutzer, oder null wenn nicht eingeloggt
    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(uid = firebaseUser.uid, email = firebaseUser.email ?: "")
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("Login fehlgeschlagen"))
            Result.success(User(uid = firebaseUser.uid, email = firebaseUser.email ?: ""))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("Registrierung fehlgeschlagen"))
            Result.success(User(uid = firebaseUser.uid, email = firebaseUser.email ?: ""))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Firebase verlangt aus Sicherheitsgründen eine frische Re-Authentifizierung,
    // bevor sensible Aktionen wie ein Passwort-Wechsel erlaubt werden
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        val firebaseUser = auth.currentUser ?: return Result.failure(Exception("Nicht eingeloggt"))
        val email = firebaseUser.email ?: return Result.failure(Exception("Keine E-Mail-Adresse hinterlegt"))

        return try {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            firebaseUser.reauthenticate(credential).await()
            firebaseUser.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}