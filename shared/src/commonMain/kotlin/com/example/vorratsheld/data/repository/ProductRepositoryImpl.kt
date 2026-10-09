package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.data.remote.CollectionNames
import dev.gitlive.firebase.firestore.FirebaseFirestore

// Produkte in der Firestore-Collection "products"
class ProductRepositoryImpl(
    firestore: FirebaseFirestore
) : ProductRepository {

    private val productsCollection = firestore.collection(CollectionNames.PRODUCTS)

    override suspend fun getProducts(userId: String): Result<List<Product>> = runCatching {
        productsCollection
            .where { "userId" equalTo userId }
            .get()
            .documents
            .map { it.data<Product>() }
    }

    override suspend fun getProductById(productId: String): Result<Product?> = runCatching {
        val snapshot = productsCollection.document(productId).get()
        if (snapshot.exists) snapshot.data<Product>() else null
    }

    override suspend fun addProduct(product: Product): Result<Unit> = runCatching {
        val docRef = productsCollection.document
        docRef.set(product.copy(id = docRef.id))
    }

    override suspend fun updateProduct(product: Product): Result<Unit> = runCatching {
        productsCollection.document(product.id).set(product)
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> = runCatching {
        productsCollection.document(productId).delete()
    }

    override suspend fun deleteAllProducts(userId: String): Result<Unit> = runCatching {
        getProducts(userId).getOrThrow().forEach { productsCollection.document(it.id).delete() }
    }
}
