package com.example.vorratsheld.data.model

import com.example.vorratsheld.data.enums.StorageLocation

// Repräsentiert ein gescanntes/gespeichertes Produkt
data class Product(
    val id: String = "",
    val userId: String = "",           // Zuordnung zum eingeloggten Nutzer
    val barcode: String = "",
    val name: String = "",
    val imageUrl: String? = null,
    val location: StorageLocation = StorageLocation.KUEHLSCHRANK,
    val expiryDate: Long = 0L,          // Ablaufdatum als Timestamp (ms)
    val isFavorite: Boolean = false,    // Landet bei Verbrauch auf Einkaufsliste
    val addedAt: Long = System.currentTimeMillis()
)