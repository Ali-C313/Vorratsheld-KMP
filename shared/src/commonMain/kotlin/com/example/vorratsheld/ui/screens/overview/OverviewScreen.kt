package com.example.vorratsheld.ui.screens.overview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.data.repository.AuthRepositoryMock
import com.example.vorratsheld.data.repository.ProductRepositoryMock
import com.example.vorratsheld.data.repository.ShoppingRepositoryMock
import com.example.vorratsheld.data.repository.UserRepositoryMock
import com.example.vorratsheld.platform.ExpiryReminderEffect
import com.example.vorratsheld.ui.navigation.Screen
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import com.example.vorratsheld.ui.viewmodel.AuthViewModel
import com.example.vorratsheld.ui.viewmodel.ProductViewModel
import com.example.vorratsheld.ui.viewmodel.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

// Stateful: holt Produkte, Nutzer und Profil vom ViewModel, kümmert sich um Navigation
@Composable
fun OverviewScreen(
    navController: NavHostController,
    productViewModel: ProductViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val products by productViewModel.products.collectAsStateWithLifecycle()
    val errorMessage by productViewModel.errorMessage.collectAsStateWithLifecycle()
    val profile by profileViewModel.uiState.collectAsStateWithLifecycle()

    // Ablauf-Erinnerungen einrichten – Android: täglicher Hintergrund-Check, iOS: lokale Benachrichtigungen
    if (currentUser != null) {
        ExpiryReminderEffect(products = products)
    }

    // Läuft bei JEDEM Betreten der Übersicht neu (z. B. Rückkehr aus ProductDetail oder Einstellungen)
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        currentUser?.let { user -> productViewModel.loadProducts(user.uid) }
        profileViewModel.loadProfile()
    }

    OverviewContent(
        products = products,
        firstName = profile.firstName,
        fullName = profile.fullName,
        photoBase64 = profile.photoBase64,
        errorMessage = errorMessage,
        onScanClick = { navController.navigate(Screen.Scan) },
        onShoppingListClick = { navController.navigate(Screen.ShoppingList) },
        onProductClick = { product ->
            navController.navigate(Screen.ProductInfo(productId = product.id))
        },
        onEditClick = { product ->
            navController.navigate(Screen.ProductDetail(productId = product.id))
        },
        onDeleteProduct = { product ->
            currentUser?.let { user -> productViewModel.deleteProduct(product, user.uid) }
        },
        onRestoreProduct = { product ->
            currentUser?.let { user -> productViewModel.restoreProduct(product, user.uid) }
        },
        onFavoriteToggle = { product ->
            currentUser?.let { user -> productViewModel.toggleFavorite(product, user.uid) }
        },
        onSettingsClick = { navController.navigate(Screen.Settings) }
    )
}

// Preview mit den Mock-Repositories statt Firebase
@Suppress("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun OverviewScreenPreview() {
    VorratsheldTheme {
        OverviewScreen(
            navController = rememberNavController(),
            productViewModel = remember {
                ProductViewModel(ProductRepositoryMock(), ShoppingRepositoryMock()).apply {
                    loadProducts(AuthRepositoryMock.MOCK_USER_ID)
                }
            },
            authViewModel = remember { AuthViewModel(AuthRepositoryMock(), UserRepositoryMock()) },
            profileViewModel = remember { ProfileViewModel(AuthRepositoryMock(), UserRepositoryMock(), ProductRepositoryMock(), ShoppingRepositoryMock()) }
        )
    }
}
