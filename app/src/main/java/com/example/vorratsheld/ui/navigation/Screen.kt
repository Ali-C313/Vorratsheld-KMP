package com.example.vorratsheld.ui.navigation

// Sealed Class für alle Navigationsziele der App
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Overview : Screen("overview")
    data object Scan : Screen("scan")

    // productId: zum Bearbeiten eines bestehenden Produkts
    // barcode: zum Anlegen eines neuen Produkts direkt nach dem Scan
    // Beide Parameter sind optional (Default ""), je nachdem, aus welchem Screen man kommt
    data object ProductDetail : Screen("product_detail?productId={productId}&barcode={barcode}") {
        fun createRouteForEdit(productId: String) = "product_detail?productId=$productId"
        fun createRouteForNewScan(barcode: String) = "product_detail?barcode=$barcode"
    }

    data object ShoppingList : Screen("shopping_list")
}