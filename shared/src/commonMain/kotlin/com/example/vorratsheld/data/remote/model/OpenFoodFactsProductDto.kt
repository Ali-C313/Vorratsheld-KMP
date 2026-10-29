package com.example.vorratsheld.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Nur die Felder, die die App braucht
@Serializable
data class OpenFoodFactsProductDto(
    @SerialName("product_name") val productName: String? = null,
    @SerialName("image_url") val imageUrl: String? = null
)
