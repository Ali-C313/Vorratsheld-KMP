package com.example.vorratsheld.ui.screens.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.vorratsheld.data.enums.StorageLocation
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.ui.components.ProductCard
import com.example.vorratsheld.ui.navigation.Screen
import com.example.vorratsheld.viewmodel.AuthViewModel
import com.example.vorratsheld.viewmodel.ProductViewModel
import androidx.compose.ui.tooling.preview.Preview

// Stateful: holt Produkte + Nutzer vom ViewModel, kümmert sich um Navigation
@Composable
fun OverviewScreen(
    navController: NavHostController,
    productViewModel: ProductViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val products by productViewModel.products.collectAsStateWithLifecycle()

    // Produkte laden, sobald der Nutzer bekannt ist
    LaunchedEffect(currentUser) {
        currentUser?.let { user -> productViewModel.loadProducts(user.uid) }
    }

    OverviewContent(
        products = products,
        onScanClick = { navController.navigate(Screen.Scan.route) },
        onShoppingListClick = { navController.navigate(Screen.ShoppingList.route) },
        onProductClick = { product ->
            navController.navigate(Screen.ProductDetail.createRouteForEdit(product.id))
        },
        onFavoriteToggle = { product ->
            currentUser?.let { user -> productViewModel.toggleFavorite(product, user.uid) }
        }
    )
}

// Stateless: reine UI, unabhängig vom ViewModel – dadurch previewbar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OverviewContent(
    products: List<Product>,
    onScanClick: () -> Unit,
    onShoppingListClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onFavoriteToggle: (Product) -> Unit
) {
    var selectedLocation by remember { mutableStateOf(StorageLocation.KUEHLSCHRANK) }

    val filteredProducts = products
        .filter { it.location == selectedLocation }
        .sortedBy { it.expiryDate }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Vorratsheld") })
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = { /* bereits auf der Übersicht */ }) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = onScanClick) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scannen")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            SecondaryTabRow(selectedTabIndex = selectedLocation.ordinal) {
                Tab(
                    selected = selectedLocation == StorageLocation.KUEHLSCHRANK,
                    onClick = { selectedLocation = StorageLocation.KUEHLSCHRANK },
                    text = { Text("Kühlschrank") }
                )
                Tab(
                    selected = selectedLocation == StorageLocation.KAMMER,
                    onClick = { selectedLocation = StorageLocation.KAMMER },
                    text = { Text("Kammer") }
                )
            }

            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Noch keine Produkte hier – scanne dein erstes Produkt!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product) },
                            onFavoriteToggle = { onFavoriteToggle(product) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OverviewContentPreview() {
    MaterialTheme {
        OverviewContent(
            products = listOf(
                Product(
                    id = "1",
                    name = "Vollmilch 1L",
                    location = StorageLocation.KUEHLSCHRANK,
                    expiryDate = System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000,
                    isFavorite = false
                ),
                Product(
                    id = "2",
                    name = "Eier (10er Pack)",
                    location = StorageLocation.KUEHLSCHRANK,
                    expiryDate = System.currentTimeMillis() + 9 * 24 * 60 * 60 * 1000,
                    isFavorite = true
                ),
                Product(
                    id = "3",
                    name = "Gouda Scheiben",
                    location = StorageLocation.KUEHLSCHRANK,
                    expiryDate = System.currentTimeMillis() + 18 * 24 * 60 * 60 * 1000,
                    isFavorite = false
                )
            ),
            onScanClick = {},
            onShoppingListClick = {},
            onProductClick = {},
            onFavoriteToggle = {}
        )
    }
}

@Preview(showBackground = true, name = "Leer")
@Composable
private fun OverviewContentEmptyPreview() {
    MaterialTheme {
        OverviewContent(
            products = emptyList(),
            onScanClick = {},
            onShoppingListClick = {},
            onProductClick = {},
            onFavoriteToggle = {}
        )
    }
}