package com.example.vorratsheld.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.data.repository.ProductRepository
import com.example.vorratsheld.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Hält die Produktliste für die Übersicht
class ProductViewModel(
    private val productRepository: ProductRepository,
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadProducts(userId: String) {
        viewModelScope.launch {
            productRepository.getProducts(userId)
                .onSuccess { products ->
                    _products.value = products
                    _errorMessage.value = null
                }
                .onFailure { e -> _errorMessage.value = e.message }
        }
    }

    fun toggleFavorite(product: Product, userId: String) {
        viewModelScope.launch {
            productRepository.updateProduct(product.copy(isFavorite = !product.isFavorite))
                .onSuccess { loadProducts(userId) }
                .onFailure { e -> _errorMessage.value = e.message }
        }
    }

    // Verschwindet sofort aus der Liste; Favoriten wandern auf die Einkaufsliste
    fun deleteProduct(product: Product, userId: String) {
        _products.value = _products.value.filter { it.id != product.id }
        viewModelScope.launch {
            productRepository.deleteProduct(product.id)
                .onSuccess { if (product.isFavorite) shoppingRepository.addFromProduct(product) }
                .onFailure { e ->
                    _errorMessage.value = e.message
                    loadProducts(userId)
                }
        }
    }

    // Rückgängig: Dokument mit derselben ID wieder anlegen
    fun restoreProduct(product: Product, userId: String) {
        viewModelScope.launch {
            productRepository.updateProduct(product)
                .onSuccess { if (product.isFavorite) shoppingRepository.removeOneFromProduct(product) }
                .onFailure { e -> _errorMessage.value = e.message }
            loadProducts(userId)
        }
    }
}
