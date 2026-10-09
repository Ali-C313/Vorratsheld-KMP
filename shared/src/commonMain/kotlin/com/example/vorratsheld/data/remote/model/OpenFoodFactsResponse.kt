package com.example.vorratsheld.data.remote.model

import kotlinx.serialization.Serializable

// Antwort der Open Food Facts API für einen einzelnen Barcode
@Serializable
data class OpenFoodFactsResponse(
    val status: Int = 0,
    val product: OpenFoodFactsProductDto? = null
)
