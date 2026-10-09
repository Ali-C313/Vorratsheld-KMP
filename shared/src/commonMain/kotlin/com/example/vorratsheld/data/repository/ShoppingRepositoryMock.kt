package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.data.model.ShoppingItem

// Für Previews: kleine Einkaufsliste, ein Artikel schon abgehakt
class ShoppingRepositoryMock : ShoppingRepository {

    private val items = mutableListOf(
        ShoppingItem(id = "1", userId = AuthRepositoryMock.MOCK_USER_ID, name = "Eier", quantity = 10),
        ShoppingItem(id = "2", userId = AuthRepositoryMock.MOCK_USER_ID, name = "Vollmilch 1L", quantity = 2),
        ShoppingItem(id = "3", userId = AuthRepositoryMock.MOCK_USER_ID, name = "Mehl", checked = true)
    )

    override suspend fun getItems(userId: String): Result<List<ShoppingItem>> =
        Result.success(items.toList())

    override suspend fun addItem(item: ShoppingItem): Result<ShoppingItem> = runCatching {
        item.copy(id = (items.size + 1).toString()).also { items += it }
    }

    override suspend fun updateItem(item: ShoppingItem): Result<Unit> = runCatching {
        val index = items.indexOfFirst { it.id == item.id }
        if (index >= 0) items[index] = item
    }

    override suspend fun deleteItem(itemId: String): Result<Unit> = runCatching {
        items.removeAll { it.id == itemId }
    }

    override suspend fun deleteAllItems(userId: String): Result<Unit> = runCatching {
        items.clear()
    }

    override suspend fun addFromProduct(product: Product): Result<Unit> = runCatching {
        items += ShoppingItem(id = (items.size + 1).toString(), userId = product.userId, name = product.name)
    }

    override suspend fun removeOneFromProduct(product: Product): Result<Unit> = runCatching {
        items.removeAll { it.name == product.name }
    }
}
