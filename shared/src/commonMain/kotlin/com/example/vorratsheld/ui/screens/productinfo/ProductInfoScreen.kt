package com.example.vorratsheld.ui.screens.productinfo

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.data.repository.ProductRepositoryMock
import com.example.vorratsheld.data.repository.ShoppingRepositoryMock
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.info_added_to_list
import com.example.vorratsheld.ui.navigation.Screen
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import com.example.vorratsheld.ui.viewmodel.ProductInfoViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

// Stateful: lädt das Produkt bei jedem Betreten neu (z. B. nach dem Bearbeiten)
@Composable
fun ProductInfoScreen(
    navController: NavHostController,
    productId: String,
    viewModel: ProductInfoViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.load(productId)
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    ProductInfoContent(
        product = uiState.product,
        isLoading = uiState.isLoading,
        snackbarHostState = snackbarHostState,
        onBackClick = { navController.popBackStack() },
        onEditClick = { navController.navigate(Screen.ProductDetail(productId = productId)) },
        onFavoriteToggle = viewModel::toggleFavorite,
        onAddToListClick = {
            viewModel.addToShoppingList {
                scope.launch {
                    val name = uiState.product?.name.orEmpty()
                    snackbarHostState.showSnackbar(getString(Res.string.info_added_to_list, name))
                }
            }
        },
        onDeleteConfirmed = { viewModel.delete { navController.popBackStack() } }
    )
}

// Preview mit den Mock-Repositories statt Firebase
@Suppress("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun ProductInfoScreenPreview() {
    VorratsheldTheme {
        ProductInfoScreen(
            navController = rememberNavController(),
            productId = "1",
            viewModel = remember {
                ProductInfoViewModel(ProductRepositoryMock(), ShoppingRepositoryMock()).apply { load("1") }
            }
        )
    }
}
