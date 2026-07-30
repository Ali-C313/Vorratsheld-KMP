package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.ScannedProductInfo
import com.example.vorratsheld.data.remote.OpenFoodFactsApi
import com.example.vorratsheld.data.remote.RetrofitInstance

// Kapselt den Zugriff auf die Open Food Facts API
class OpenFoodFactsRepository(
    private val api: OpenFoodFactsApi = RetrofitInstance.api
) {

    // Gibt null zurück, wenn das Produkt nicht in der Datenbank gefunden wurde
    // (dann trägt der Nutzer den Namen im ProductDetailScreen manuell ein)
    suspend fun fetchProductByBarcode(barcode: String): Result<ScannedProductInfo?> {
        return try {
            val response = api.getProduct(barcode)
            if (response.status == 1 && response.product != null) {
                Result.success(
                    ScannedProductInfo(
                        barcode = barcode,
                        name = response.product.productName ?: "",
                        imageUrl = response.product.imageUrl
                    )
                )
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}