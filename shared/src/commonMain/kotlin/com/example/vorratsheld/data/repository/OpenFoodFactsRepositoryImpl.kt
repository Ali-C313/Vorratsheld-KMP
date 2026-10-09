package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.ScannedProductInfo
import com.example.vorratsheld.data.remote.OpenFoodFactsApi

// Produktdaten zum Barcode von Open Food Facts
class OpenFoodFactsRepositoryImpl(
    private val api: OpenFoodFactsApi
) : OpenFoodFactsRepository {

    override suspend fun fetchProductByBarcode(barcode: String): Result<ScannedProductInfo?> = runCatching {
        val response = api.getProduct(barcode)
        val product = response.product
        if (response.status == 1 && product != null) {
            ScannedProductInfo(
                barcode = barcode,
                name = product.productName ?: "",
                imageUrl = product.imageUrl
            )
        } else {
            null
        }
    }
}
