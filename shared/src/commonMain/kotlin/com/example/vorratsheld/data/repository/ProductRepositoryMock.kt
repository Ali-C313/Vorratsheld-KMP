package com.example.vorratsheld.data.repository

import com.example.vorratsheld.data.enums.StorageLocation
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.util.DateHelper

// Für Previews: ein paar Produkte mit unterschiedlichem Ablaufdatum
class ProductRepositoryMock : ProductRepository {

    private val products = mutableListOf(
        Product(
            id = "1",
            userId = AuthRepositoryMock.MOCK_USER_ID,
            barcode = "4008400301012",
            name = "Vollmilch 1L",
            location = StorageLocation.KUEHLSCHRANK,
            expiryDate = DateHelper.nowMillis() + 2 * DAY_MS,
            quantity = 2,
            isFavorite = true
        ),
        Product(
            id = "2",
            userId = AuthRepositoryMock.MOCK_USER_ID,
            name = "Gouda Scheiben",
            location = StorageLocation.KUEHLSCHRANK,
            expiryDate = DateHelper.nowMillis() + 8 * DAY_MS
        ),
        Product(
            id = "3",
            userId = AuthRepositoryMock.MOCK_USER_ID,
            name = "Eier (10er Pack)",
            location = StorageLocation.KUEHLSCHRANK,
            expiryDate = DateHelper.nowMillis() - DAY_MS,
            isFavorite = true
        ),
        Product(
            id = "4",
            userId = AuthRepositoryMock.MOCK_USER_ID,
            name = "Spaghetti",
            location = StorageLocation.KAMMER,
            expiryDate = DateHelper.nowMillis() + 200 * DAY_MS,
            quantity = 3
        ),
        Product(
            id = "5",
            userId = AuthRepositoryMock.MOCK_USER_ID,
            name = "Tomaten passiert",
            location = StorageLocation.KAMMER,
            expiryDate = DateHelper.nowMillis() + 40 * DAY_MS
        )
    )

    override suspend fun getProducts(userId: String): Result<List<Product>> =
        Result.success(products.toList())

    override suspend fun getProductById(productId: String): Result<Product?> =
        Result.success(products.firstOrNull { it.id == productId })

    override suspend fun addProduct(product: Product): Result<Unit> = runCatching {
        products += product.copy(id = (products.size + 1).toString())
    }

    override suspend fun updateProduct(product: Product): Result<Unit> = runCatching {
        products.removeAll { it.id == product.id }
        products += product
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> = runCatching {
        products.removeAll { it.id == productId }
    }

    override suspend fun deleteAllProducts(userId: String): Result<Unit> = runCatching {
        products.clear()
    }

    private companion object {
        const val DAY_MS = 24L * 60 * 60 * 1000
    }
}
