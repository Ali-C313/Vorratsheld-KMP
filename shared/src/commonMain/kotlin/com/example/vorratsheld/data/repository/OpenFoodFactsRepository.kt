package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.ScannedProductInfo

// Produktdaten zum gescannten Barcode
interface OpenFoodFactsRepository {
    // null, wenn der Barcode nicht in der Datenbank ist (dann trägt der Nutzer den Namen selbst ein)
    suspend fun fetchProductByBarcode(barcode: String): Result<ScannedProductInfo?>
}
