package com.example.vorratsheld.ui.navigation

import kotlinx.serialization.Serializable

// Alle Navigationsziele der App, typsicher statt String-Routen
@Serializable
sealed interface Screen {
    @Serializable
    data object Splash : Screen

    @Serializable
    data object Onboarding : Screen

    @Serializable
    data object Login : Screen

    @Serializable
    data object Register : Screen

    @Serializable
    data object Overview : Screen

    @Serializable
    data object Scan : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object EditProfile : Screen

    @Serializable
    data object Language : Screen

    @Serializable
    data object Help : Screen

    @Serializable
    data object SecuritySettings : Screen

    // type = Name aus LegalType (AGB, Datenschutz, Impressum)
    @Serializable
    data class Legal(val type: String) : Screen

    // productId: zum Bearbeiten eines bestehenden Produkts
    // barcode: zum Anlegen eines neuen Produkts direkt nach dem Scan
    // Beide optional (Default ""), je nachdem, aus welchem Screen man kommt
    @Serializable
    data class ProductDetail(
        val productId: String = "",
        val barcode: String = ""
    ) : Screen

    // Nur-Lesen-Ansicht beim Tippen auf ein Produkt
    @Serializable
    data class ProductInfo(val productId: String) : Screen

    @Serializable
    data object ShoppingList : Screen
}
