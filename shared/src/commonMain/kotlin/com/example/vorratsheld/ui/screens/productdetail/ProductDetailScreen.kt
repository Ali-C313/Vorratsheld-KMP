package com.example.vorratsheld.ui.screens.productdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.data.repository.AuthRepositoryMock
import com.example.vorratsheld.data.repository.OpenFoodFactsRepositoryMock
import com.example.vorratsheld.data.repository.ProductRepositoryMock
import com.example.vorratsheld.data.repository.ShoppingRepositoryMock
import com.example.vorratsheld.data.repository.UserRepositoryMock
import com.example.vorratsheld.ui.navigation.Screen
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import com.example.vorratsheld.ui.viewmodel.AuthViewModel
import com.example.vorratsheld.ui.viewmodel.ProductDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

// Stateful: lädt je nach Modus (Bearbeiten/Neu-Scan) die passenden Daten und kümmert sich um Navigation
@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    productId: String,
    barcode: String,
    viewModel: ProductDetailViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        when {
            productId.isNotEmpty() -> viewModel.loadForEdit(productId)
            barcode.isNotEmpty() -> viewModel.loadForNewScan(barcode)
        }
    }

    ProductDetailContent(
        uiState = uiState,
        autoScanExpiry = barcode.isNotEmpty(),
        onNameChange = viewModel::updateName,
        onLocationChange = viewModel::updateLocation,
        onQuantityChange = viewModel::changeQuantity,
        onExpiryDateChange = viewModel::updateExpiryDate,
        onFavoriteToggle = viewModel::toggleFavorite,
        onBackClick = { navController.popBackStack() },
        onSaveClick = {
            currentUser?.let { user ->
                viewModel.save(user.uid) { navController.popBackStack() }
            }
        },
        onDeleteConfirmed = {
            currentUser?.let { user ->
                // Info-Screen darunter zeigt das gelöschte Produkt nicht mehr, deshalb bis zur Übersicht zurück
                viewModel.delete(user.uid) { navController.popBackStack(Screen.Overview, inclusive = false) }
            }
        }
    )
}

// Preview mit den Mock-Repositories statt Firebase
@Suppress("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun ProductDetailScreenPreview() {
    VorratsheldTheme {
        ProductDetailScreen(
            navController = rememberNavController(),
            productId = "1",
            barcode = "",
            viewModel = remember {
                ProductDetailViewModel(ProductRepositoryMock(), OpenFoodFactsRepositoryMock(), ShoppingRepositoryMock()).apply {
                    loadForEdit("1")
                }
            },
            authViewModel = remember { AuthViewModel(AuthRepositoryMock(), UserRepositoryMock()) }
        )
    }
}
