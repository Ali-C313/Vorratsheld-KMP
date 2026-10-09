package com.example.vorratsheld.data.model

import com.example.vorratsheld.data.enums.StorageLocation
import com.example.vorratsheld.util.DateHelper
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Repräsentiert ein gescanntes/gespeichertes Produkt
@Serializable
data class Product(
    val id: String = "",
    val userId: String = "",           // Zuordnung zum eingeloggten Nutzer
    val barcode: String = "",
    val name: String = "",
    val imageUrl: String? = null,
    val location: StorageLocation = StorageLocation.KUEHLSCHRANK,
    val expiryDate: Long = 0L,          // Ablaufdatum als Timestamp (ms)
    val quantity: Int = 1,
    // Feld heißt in Firestore "favorite" (so hat es die alte Android-Version gespeichert)
    @SerialName("favorite")
    val isFavorite: Boolean = false,    // Landet bei Verbrauch auf Einkaufsliste
    val addedAt: Long = DateHelper.nowMillis()
)
