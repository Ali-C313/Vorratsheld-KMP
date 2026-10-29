package com.example.vorratsheld.ui.shoppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vorratsheld.data.model.ShoppingItem
import com.example.vorratsheld.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Änderungen erscheinen sofort, Firestore wird im Hintergrund nachgezogen
class ShoppingListViewModel(
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShoppingListUiState())
    val uiState: StateFlow<ShoppingListUiState> = _uiState.asStateFlow()

    private var userId = ""

    fun load(userId: String) {
        this.userId = userId
        viewModelScope.launch {
            shoppingRepository.getItems(userId)
                .onSuccess { items ->
                    _uiState.update { it.copy(items = items.sortedBy { item -> item.addedAt }, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    fun addItem(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || userId.isEmpty()) return
        viewModelScope.launch {
            shoppingRepository.addItem(ShoppingItem(userId = userId, name = trimmed))
                .onSuccess { item -> _uiState.update { it.copy(items = it.items + item) } }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }

    fun toggleChecked(item: ShoppingItem) = save(item.copy(checked = !item.checked))

    fun changeQuantity(item: ShoppingItem, delta: Int) {
        val quantity = (item.quantity + delta).coerceIn(1, 99)
        if (quantity != item.quantity) save(item.copy(quantity = quantity))
    }

    fun delete(item: ShoppingItem) {
        _uiState.update { state -> state.copy(items = state.items.filter { it.id != item.id }) }
        viewModelScope.launch {
            shoppingRepository.deleteItem(item.id)
                .onFailure { e -> showErrorAndReload(e) }
        }
    }

    fun clearDone() {
        val done = _uiState.value.doneItems
        _uiState.update { state -> state.copy(items = state.items.filter { !it.checked }) }
        viewModelScope.launch {
            done.map { shoppingRepository.deleteItem(it.id) }
                .firstOrNull { it.isFailure }
                ?.exceptionOrNull()
                ?.let { e -> showErrorAndReload(e) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun save(item: ShoppingItem) {
        _uiState.update { state -> state.copy(items = state.items.map { if (it.id == item.id) item else it }) }
        viewModelScope.launch {
            shoppingRepository.updateItem(item)
                .onFailure { e -> showErrorAndReload(e) }
        }
    }

    // Lokale Änderung zurückdrehen, indem der Stand aus Firestore neu geladen wird
    private fun showErrorAndReload(e: Throwable) {
        _uiState.update { it.copy(errorMessage = e.message) }
        load(userId)
    }
}
