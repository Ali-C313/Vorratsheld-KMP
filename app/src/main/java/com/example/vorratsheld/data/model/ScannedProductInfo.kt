package com.example.vorratsheld.data.model

// Zwischenergebnis eines Barcode-Scans, bevor daraus ein vollständiges Product wird
// (userId, location und expiryDate kennt die API nicht, die trägt der Nutzer selbst ein)
data class ScannedProductInfo(
    val barcode: String,
    val name: String,
    val imageUrl: String?
)