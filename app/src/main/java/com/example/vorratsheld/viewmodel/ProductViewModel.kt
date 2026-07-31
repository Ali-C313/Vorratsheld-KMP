package com.example.vorratsheld.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Hält die Produktliste für die Übersicht-Screens
class ProductViewModel(
    private val productRepository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadProducts(userId: String) {
        viewModelScope.launch {
            try {
                _products.value = productRepository.getProducts(userId)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun addProduct(product: Product, userId: String) {
        viewModelScope.launch {
            try {
                productRepository.addProduct(product)
                loadProducts(userId)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    // Schaltet den Favoriten-Status eines Produkts um (z. B. für die Einkaufsliste)
    fun toggleFavorite(product: Product, userId: String) {
        viewModelScope.launch {
            try {
                productRepository.updateProduct(product.copy(isFavorite = !product.isFavorite))
                loadProducts(userId)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}