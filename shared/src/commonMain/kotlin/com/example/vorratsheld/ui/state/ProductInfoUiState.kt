package com.example.vorratsheld.ui.state

import com.example.vorratsheld.data.model.Product

// Zustand der Produktansicht
data class ProductInfoUiState(
    val product: Product? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
