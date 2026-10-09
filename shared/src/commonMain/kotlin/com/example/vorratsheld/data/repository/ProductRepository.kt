package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.Product

// Produkte im Vorrat des eingeloggten Nutzers
interface ProductRepository {
    suspend fun getProducts(userId: String): Result<List<Product>>

    // null, wenn es das Produkt nicht (mehr) gibt
    suspend fun getProductById(productId: String): Result<Product?>

    // id wird beim Anlegen vergeben
    suspend fun addProduct(product: Product): Result<Unit>

    // Legt das Dokument auch neu an, wenn es fehlt (für "Rückgängig")
    suspend fun updateProduct(product: Product): Result<Unit>

    suspend fun deleteProduct(productId: String): Result<Unit>

    suspend fun deleteAllProducts(userId: String): Result<Unit>
}
