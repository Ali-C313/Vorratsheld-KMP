package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.data.model.ShoppingItem

// Einkaufsliste, unabhängig vom Vorrat
interface ShoppingRepository {
    suspend fun getItems(userId: String): Result<List<ShoppingItem>>

    // Gibt den Eintrag mit der neuen id zurück
    suspend fun addItem(item: ShoppingItem): Result<ShoppingItem>

    suspend fun updateItem(item: ShoppingItem): Result<Unit>

    suspend fun deleteItem(itemId: String): Result<Unit>

    suspend fun deleteAllItems(userId: String): Result<Unit>

    // Steht der Artikel schon offen auf der Liste, wird nur die Menge erhöht
    suspend fun addFromProduct(product: Product): Result<Unit>

    // Gegenstück zu addFromProduct, z. B. beim Rückgängigmachen
    suspend fun removeOneFromProduct(product: Product): Result<Unit>
}
