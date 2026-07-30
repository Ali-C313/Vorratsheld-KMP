package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Kapselt den Zugriff auf die Firestore-Collection "products"
class ProductRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val productsCollection = firestore.collection("products")

    // Lädt alle Produkte des eingeloggten Nutzers
    suspend fun getProducts(userId: String): List<Product> {
        val snapshot = productsCollection
            .whereEqualTo("userId", userId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Product::class.java) }
    }

    // Lädt ein einzelnes Produkt anhand seiner ID (z. B. zum Bearbeiten im ProductDetailScreen)
    suspend fun getProductById(productId: String): Product? {
        val snapshot = productsCollection.document(productId).get().await()
        return snapshot.toObject(Product::class.java)
    }

    // Legt ein neues Produkt an (id wird von Firestore vergeben)
    suspend fun addProduct(product: Product) {
        val docRef = productsCollection.document()
        val productWithId = product.copy(id = docRef.id)
        docRef.set(productWithId).await()
    }

    suspend fun updateProduct(product: Product) {
        productsCollection.document(product.id).set(product).await()
    }

    suspend fun deleteProduct(productId: String) {
        productsCollection.document(productId).delete().await()
    }
}