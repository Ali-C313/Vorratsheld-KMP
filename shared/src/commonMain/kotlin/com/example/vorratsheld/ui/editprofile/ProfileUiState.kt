package com.example.vorratsheld.ui.editprofile

// Zustand für Einstellungen (Profil-Card) und "Profil bearbeiten"
data class ProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",          // aktuelle Adresse aus Firebase Auth
    val newEmail: String = "",       // Eingabefeld, kann geändert werden
    val photoBase64: String? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val message: String? = null,     // Erfolgsmeldung für die Snackbar
    val errorMessage: String? = null
) {
    val fullName: String get() = "$firstName $lastName".trim()
    val emailChanged: Boolean get() = newEmail.trim().isNotEmpty() && newEmail.trim() != email
}
