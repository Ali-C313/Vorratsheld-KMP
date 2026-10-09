package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.ScannedProductInfo

// Für Previews: jeder Barcode ist Vollmilch
class OpenFoodFactsRepositoryMock : OpenFoodFactsRepository {
    override suspend fun fetchProductByBarcode(barcode: String): Result<ScannedProductInfo?> =
        Result.success(ScannedProductInfo(barcode = barcode, name = "Vollmilch 1L", imageUrl = null))
}
