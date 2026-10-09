package com.example.vorratsheld.data.model

// Repräsentiert den eingeloggten Nutzer
data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = ""   // "Vorname Nachname" aus Firebase Auth (Fallback fürs Profil)
)
