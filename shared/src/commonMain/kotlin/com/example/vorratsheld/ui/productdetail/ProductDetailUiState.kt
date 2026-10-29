package com.example.vorratsheld.ui.productdetail

import com.example.vorratsheld.data.enums.StorageLocation
import com.example.vorratsheld.util.DateHelper

// Zustand des ProductDetailScreen, deckt sowohl "neu anlegen" als auch "bearbeiten" ab
data class ProductDetailUiState(
    val productId: String = "",
    val barcode: String = "",
    val name: String = "",
    val imageUrl: String? = null,
    val location: StorageLocation = StorageLocation.KUEHLSCHRANK,
    val expiryDate: Long = DateHelper.nowMillis() + 7L * 24 * 60 * 60 * 1000, // Default: in 1 Woche
    val isFavorite: Boolean = false,
    val quantity: Int = 1,
    val addedAt: Long = DateHelper.nowMillis(),
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
