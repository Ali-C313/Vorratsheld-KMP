package com.example.vorratsheld.ui.productinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vorratsheld.data.repository.ProductRepository
import com.example.vorratsheld.data.repository.ShoppingRepository
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.error_delete_failed
import com.example.vorratsheld.resources.error_product_not_found
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

// Nur-Lesen-Ansicht eines Produkts mit ein paar Schnellaktionen
class ProductInfoViewModel(
    private val productRepository: ProductRepository,
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductInfoUiState())
    val uiState: StateFlow<ProductInfoUiState> = _uiState.asStateFlow()

    fun load(productId: String) {
        viewModelScope.launch {
            productRepository.getProductById(productId)
                .onSuccess { product ->
                    val notFound = if (product == null) getString(Res.string.error_product_not_found) else null
                    _uiState.update { it.copy(product = product, isLoading = false, errorMessage = notFound) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    fun toggleFavorite() {
        val product = _uiState.value.product ?: return
        val updated = product.copy(isFavorite = !product.isFavorite)
        _uiState.update { it.copy(product = updated) }
        viewModelScope.launch {
            productRepository.updateProduct(updated)
                .onFailure { e ->
                    _uiState.update { it.copy(product = product, errorMessage = e.message) }
                }
        }
    }

    fun addToShoppingList(onAdded: () -> Unit) {
        val product = _uiState.value.product ?: return
        viewModelScope.launch {
            shoppingRepository.addFromProduct(product)
                .onSuccess { onAdded() }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val product = _uiState.value.product ?: return
        viewModelScope.launch {
            productRepository.deleteProduct(product.id)
                .onSuccess {
                    if (product.isFavorite) shoppingRepository.addFromProduct(product)
                    onDeleted()
                }
                .onFailure { e ->
                    val message = getString(Res.string.error_delete_failed, e.message ?: "")
                    _uiState.update { it.copy(errorMessage = message) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
