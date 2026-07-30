package com.example.vorratsheld.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vorratsheld.data.enums.StorageLocation
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.data.repository.OpenFoodFactsRepository
import com.example.vorratsheld.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Zustand des ProductDetailScreen, deckt sowohl "neu anlegen" als auch "bearbeiten" ab
data class ProductDetailUiState(
    val productId: String = "",
    val barcode: String = "",
    val name: String = "",
    val imageUrl: String? = null,
    val location: StorageLocation = StorageLocation.KUEHLSCHRANK,
    val expiryDate: Long = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000, // Default: in 1 Woche
    val isFavorite: Boolean = false,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProductDetailViewModel(
    private val productRepository: ProductRepository = ProductRepository(),
    private val openFoodFactsRepository: OpenFoodFactsRepository = OpenFoodFactsRepository()
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
            val product = productRepository.getProductById(productId)
            if (product != null) {
                _uiState.value = ProductDetailUiState(
                    productId = product.id,
                    barcode = product.barcode,
                    name = product.name,
                    imageUrl = product.imageUrl,
                    location = product.location,
                    expiryDate = product.expiryDate,
                    isFavorite = product.isFavorite,
                    isEditMode = true
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Produkt nicht gefunden")
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
            isFavorite = state.isFavorite
        )
        viewModelScope.launch {
            if (state.isEditMode) {
                productRepository.updateProduct(product)
            } else {
                productRepository.addProduct(product)
            }
            onSaved()
        }
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            productRepository.deleteProduct(_uiState.value.productId)
            onDeleted()
        }
    }
}