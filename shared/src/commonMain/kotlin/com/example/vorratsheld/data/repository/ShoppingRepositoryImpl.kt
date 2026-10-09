package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.data.model.ShoppingItem
import com.example.vorratsheld.data.remote.CollectionNames
import dev.gitlive.firebase.firestore.FirebaseFirestore

// Einkaufsliste in der Firestore-Collection "shoppingItems"
class ShoppingRepositoryImpl(
    firestore: FirebaseFirestore
) : ShoppingRepository {

    private val itemsCollection = firestore.collection(CollectionNames.SHOPPING_ITEMS)

    override suspend fun getItems(userId: String): Result<List<ShoppingItem>> = runCatching {
        loadItems(userId)
    }

    override suspend fun addItem(item: ShoppingItem): Result<ShoppingItem> = runCatching {
        val docRef = itemsCollection.document
        val saved = item.copy(id = docRef.id)
        docRef.set(saved)
        saved
    }

    override suspend fun updateItem(item: ShoppingItem): Result<Unit> = runCatching {
        itemsCollection.document(item.id).set(item)
    }

    override suspend fun deleteItem(itemId: String): Result<Unit> = runCatching {
        itemsCollection.document(itemId).delete()
    }

    override suspend fun deleteAllItems(userId: String): Result<Unit> = runCatching {
        loadItems(userId).forEach { itemsCollection.document(it.id).delete() }
    }

    override suspend fun addFromProduct(product: Product): Result<Unit> = runCatching {
        val existing = findOpenItem(product)
        if (existing != null) {
            itemsCollection.document(existing.id).set(existing.copy(quantity = existing.quantity + 1))
        } else {
            addItem(ShoppingItem(userId = product.userId, name = product.name, imageUrl = product.imageUrl)).getOrThrow()
        }
    }

    override suspend fun removeOneFromProduct(product: Product): Result<Unit> = runCatching {
        val existing = findOpenItem(product) ?: return@runCatching
        if (existing.quantity > 1) {
            itemsCollection.document(existing.id).set(existing.copy(quantity = existing.quantity - 1))
        } else {
            itemsCollection.document(existing.id).delete()
        }
    }

    private suspend fun loadItems(userId: String): List<ShoppingItem> =
        itemsCollection
            .where { "userId" equalTo userId }
            .get()
            .documents
            .map { it.data<ShoppingItem>() }

    private suspend fun findOpenItem(product: Product): ShoppingItem? =
        loadItems(product.userId).firstOrNull { !it.checked && it.name.equals(product.name, ignoreCase = true) }
}
