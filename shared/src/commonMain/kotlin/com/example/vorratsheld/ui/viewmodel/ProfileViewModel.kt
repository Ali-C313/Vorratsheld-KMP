package com.example.vorratsheld.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vorratsheld.data.model.UserProfile
import com.example.vorratsheld.data.repository.AuthRepository
import com.example.vorratsheld.data.repository.ProductRepository
import com.example.vorratsheld.data.repository.ShoppingRepository
import com.example.vorratsheld.data.repository.UserRepository
import com.example.vorratsheld.data.settings.AppLockSettings
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.error_password_required
import com.example.vorratsheld.resources.profile_email_verification_sent
import com.example.vorratsheld.resources.profile_password_changed
import com.example.vorratsheld.resources.profile_saved
import com.example.vorratsheld.ui.state.ProfileUiState
import kotlin.io.encoding.Base64
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

// Profil, E-Mail, Passwort und Konto löschen
class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val user = authRepository.getCurrentUser() ?: return
        // Fallback aus Firebase Auth, falls Firestore (noch) nichts liefert
        val fallbackFirst = user.displayName.substringBefore(" ").trim()
        val fallbackLast = user.displayName.substringAfter(" ", "").trim()

        viewModelScope.launch {
            userRepository.getProfile(user.uid)
                .onSuccess { profile ->
                    _uiState.value = _uiState.value.copy(
                        firstName = profile.firstName.ifBlank { fallbackFirst },
                        lastName = profile.lastName.ifBlank { fallbackLast },
                        photoBase64 = profile.photoBase64,
                        email = user.email,
                        newEmail = user.email,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        firstName = fallbackFirst,
                        lastName = fallbackLast,
                        email = user.email,
                        newEmail = user.email,
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
        }
    }

    fun updateFirstName(value: String) {
        _uiState.value = _uiState.value.copy(firstName = value)
    }

    fun updateLastName(value: String) {
        _uiState.value = _uiState.value.copy(lastName = value)
    }

    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(newEmail = value)
    }

    // Bild kommt schon verkleinert aus dem Screen (FileKit)
    fun setPhoto(bytes: ByteArray) {
        _uiState.value = _uiState.value.copy(photoBase64 = Base64.encode(bytes))
    }

    fun removePhoto() {
        _uiState.value = _uiState.value.copy(photoBase64 = null)
    }

    fun showError(message: String) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(message = null, errorMessage = null)
    }

    // currentPassword wird nur gebraucht, wenn sich die E-Mail ändert
    fun saveProfile(currentPassword: String) {
        val user = authRepository.getCurrentUser() ?: return
        val state = _uiState.value

        viewModelScope.launch {
            if (state.emailChanged && currentPassword.isBlank()) {
                _uiState.value = state.copy(errorMessage = getString(Res.string.error_password_required))
                return@launch
            }

            _uiState.value = state.copy(isSaving = true)
            try {
                // Name zusätzlich in Firebase Auth – damit ist er auch ohne Firestore-Zugriff gespeichert
                authRepository.updateDisplayName(state.fullName).getOrThrow()
                userRepository.saveProfile(
                    user.uid,
                    UserProfile(
                        firstName = state.firstName.trim(),
                        lastName = state.lastName.trim(),
                        photoBase64 = state.photoBase64
                    )
                ).getOrThrow()

                val message = if (state.emailChanged) {
                    val newEmail = state.newEmail.trim()
                    authRepository.changeEmail(currentPassword, newEmail).getOrThrow()
                    getString(Res.string.profile_email_verification_sent, newEmail)
                } else {
                    getString(Res.string.profile_saved)
                }
                _uiState.value = _uiState.value.copy(isSaving = false, message = message)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = e.message)
            }
        }
    }

    // onSuccess leert die Passwortfelder im Screen
    fun changePassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.changePassword(currentPassword, newPassword)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(message = getString(Res.string.profile_password_changed))
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = e.message)
                }
        }
    }

    // Reihenfolge: Passwort prüfen -> Produkte, Einkaufsliste + Profil löschen -> Account löschen
    fun deleteAccount(currentPassword: String, onDeleted: () -> Unit) {
        val user = authRepository.getCurrentUser() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                authRepository.confirmPassword(currentPassword).getOrThrow()
                productRepository.deleteAllProducts(user.uid).getOrThrow()
                shoppingRepository.deleteAllItems(user.uid).getOrThrow()
                userRepository.deleteProfile(user.uid).getOrThrow()
                authRepository.deleteUser().getOrThrow()
                AppLockSettings.clear()
                onDeleted()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = e.message)
            }
        }
    }
}
