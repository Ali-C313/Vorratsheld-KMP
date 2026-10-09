package com.example.vorratsheld.ui.state

import com.example.vorratsheld.data.model.ShoppingItem

// Zustand der Einkaufsliste, aufgeteilt in offen und erledigt
data class ShoppingListUiState(
    val items: List<ShoppingItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val openItems: List<ShoppingItem> get() = items.filter { !it.checked }
    val doneItems: List<ShoppingItem> get() = items.filter { it.checked }
}
