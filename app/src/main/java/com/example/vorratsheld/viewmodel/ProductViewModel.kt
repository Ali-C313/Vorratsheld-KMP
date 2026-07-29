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

    fun loadProducts(userId: String) {
        viewModelScope.launch {
            _products.value = productRepository.getProducts(userId)
        }
    }

    fun addProduct(product: Product, userId: String) {
        viewModelScope.launch {
            productRepository.addProduct(product)
            loadProducts(userId)
        }
    }
}
