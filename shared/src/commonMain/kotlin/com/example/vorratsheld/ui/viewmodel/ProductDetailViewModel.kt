package com.example.vorratsheld.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vorratsheld.data.enums.StorageLocation
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.data.repository.OpenFoodFactsRepository
import com.example.vorratsheld.data.repository.ProductRepository
import com.example.vorratsheld.data.repository.ShoppingRepository
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.error_delete_failed
import com.example.vorratsheld.resources.error_product_not_found
import com.example.vorratsheld.resources.error_save_failed
import com.example.vorratsheld.ui.state.ProductDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

// Neues Produkt nach dem Scan anlegen oder bestehendes bearbeiten
class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val openFoodFactsRepository: OpenFoodFactsRepository,
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    // Neues Produkt nach einem Scan: Barcode ist bekannt, Rest wird von Open Food Facts vorbefüllt
    fun loadForNewScan(barcode: String) {
        _uiState.value = _uiState.value.copy(barcode = barcode, isLoading = true)
        viewModelScope.launch {
            openFoodFactsRepository.fetchProductByBarcode(barcode)
                .onSuccess { info ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        name = info?.name ?: "",
                        imageUrl = info?.imageUrl
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
                }
        }
    }

    // Bestehendes Produkt zum Bearbeiten laden
    fun loadForEdit(productId: String) {
        _uiState.value = _uiState.value.copy(productId = productId, isLoading = true, isEditMode = true)
        viewModelScope.launch {
            productRepository.getProductById(productId)
                .onSuccess { product ->
                    if (product != null) {
                        _uiState.value = ProductDetailUiState(
                            productId = product.id,
                            barcode = product.barcode,
                            name = product.name,
                            imageUrl = product.imageUrl,
                            location = product.location,
                            expiryDate = product.expiryDate,
                            isFavorite = product.isFavorite,
                            quantity = product.quantity,
                            addedAt = product.addedAt,
                            isEditMode = true
                        )
                    } else {
                        val message = getString(Res.string.error_product_not_found)
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
                    }
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
                }
        }
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateLocation(location: StorageLocation) {
        _uiState.value = _uiState.value.copy(location = location)
    }

    fun updateExpiryDate(dateMillis: Long) {
        _uiState.value = _uiState.value.copy(expiryDate = dateMillis)
    }

    fun changeQuantity(delta: Int) {
        val quantity = (_uiState.value.quantity + delta).coerceIn(1, 99)
        _uiState.value = _uiState.value.copy(quantity = quantity)
    }

    fun toggleFavorite() {
        _uiState.value = _uiState.value.copy(isFavorite = !_uiState.value.isFavorite)
    }

    fun save(userId: String, onSaved: () -> Unit) {
        val state = _uiState.value
        val product = Product(
            id = state.productId,
            userId = userId,
            barcode = state.barcode,
            name = state.name,
            imageUrl = state.imageUrl,
            location = state.location,
            expiryDate = state.expiryDate,
            isFavorite = state.isFavorite,
            quantity = state.quantity,
            addedAt = state.addedAt
        )
        viewModelScope.launch {
            val result = if (state.isEditMode) {
                productRepository.updateProduct(product)
            } else {
                productRepository.addProduct(product)
            }
            result
                .onSuccess { onSaved() }
                .onFailure { e ->
                    // Fehler anzeigen statt abstürzen, Nutzer bleibt auf dem Screen
                    _uiState.value = _uiState.value.copy(errorMessage = getString(Res.string.error_save_failed, e.message ?: ""))
                }
        }
    }

    fun delete(userId: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            productRepository.deleteProduct(state.productId)
                .onSuccess {
                    // Favoriten landen beim Löschen auf der Einkaufsliste
                    if (state.isFavorite) {
                        shoppingRepository.addFromProduct(
                            Product(userId = userId, name = state.name, imageUrl = state.imageUrl)
                        )
                    }
                    onDeleted()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(errorMessage = getString(Res.string.error_delete_failed, e.message ?: ""))
                }
        }
    }
}
