package com.example.vorratsheld.ui.navigation

// Sealed Class für alle Navigationsziele der App
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Overview : Screen("overview")
    data object Scan : Screen("scan")
    data object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    data object ShoppingList : Screen("shopping_list")
}
