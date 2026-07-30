package com.example.vorratsheld.data.remote

import com.squareup.moshi.Json

// Antwort der Open Food Facts API für einen einzelnen Barcode
data class OpenFoodFactsResponse(
    val status: Int = 0,
    val product: OpenFoodFactsProductDto? = null
)

// Nur die Felder, die wir für Vorratsheld tatsächlich brauchen
data class OpenFoodFactsProductDto(
    @Json(name = "product_name") val productName: String? = null,
    @Json(name = "image_url") val imageUrl: String? = null
)