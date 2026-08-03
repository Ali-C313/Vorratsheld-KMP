package com.example.vorratsheld.ui.screens.overview

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.vorratsheld.data.enums.StorageLocation
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.ui.components.ProductCard
import com.example.vorratsheld.ui.navigation.Screen
import com.example.vorratsheld.viewmodel.AuthViewModel
import com.example.vorratsheld.viewmodel.ProductViewModel
import com.example.vorratsheld.worker.WorkScheduler
import kotlinx.coroutines.launch

// Stateful: holt Produkte + Nutzer vom ViewModel, kümmert sich um Navigation
@Composable
fun OverviewScreen(
    navController: NavHostController,
    productViewModel: ProductViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val products by productViewModel.products.collectAsStateWithLifecycle()
    val errorMessage by productViewModel.errorMessage.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Ergebnis wird nicht extra behandelt: Ohne Erlaubnis zeigt WorkManager schlicht keine Notification */ }

    // Produkte laden, Ablauf-Check einplanen und Notification-Berechtigung anfragen,
    // sobald der Nutzer bekannt ist
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            WorkScheduler.schedulePeriodicExpiryCheck(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val alreadyGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (!alreadyGranted) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    // Wichtig: läuft bei JEDEM Betreten der Übersicht neu (z. B. Rückkehr aus ProductDetail),
    // nicht nur beim ersten Erscheinen – sonst zeigt die Liste nach dem Speichern noch alte Daten
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        currentUser?.let { user -> productViewModel.loadProducts(user.uid) }
    }

    OverviewContent(
        products = products,
        errorMessage = errorMessage,
        onScanClick = { navController.navigate(Screen.Scan.route) },
        onShoppingListClick = { navController.navigate(Screen.ShoppingList.route) },
        onProductClick = { product ->
            navController.navigate(Screen.ProductDetail.createRouteForEdit(product.id))
        },
        onFavoriteToggle = { product ->
            currentUser?.let { user -> productViewModel.toggleFavorite(product, user.uid) }
        },
        onProfileClick = { navController.navigate(Screen.Profile.route) }
    )
}

// Stateless: reine UI, unabhängig vom ViewModel – dadurch previewbar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OverviewContent(
    products: List<Product>,
    errorMessage: String?,
    onScanClick: () -> Unit,
    onShoppingListClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onFavoriteToggle: (Product) -> Unit,
    onProfileClick: () -> Unit
) {
    var selectedLocation by remember { mutableStateOf(StorageLocation.KUEHLSCHRANK) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(errorMessage) {
        errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    val filteredProducts = products
        .filter { it.location == selectedLocation }
        .sortedBy { it.expiryDate }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vorratsheld") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Profil")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = onShoppingListClick) {
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
                            onFavoriteToggle = {
                                onFavoriteToggle(product)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Favorit aktualisiert: ${product.name}")
                                }
                            }
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
            errorMessage = null,
            onScanClick = {},
            onShoppingListClick = {},
            onProductClick = {},
            onFavoriteToggle = {},
            onProfileClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Leer")
@Composable
private fun OverviewContentEmptyPreview() {
    MaterialTheme {
        OverviewContent(
            products = emptyList(),
            errorMessage = null,
            onScanClick = {},
            onShoppingListClick = {},
            onProductClick = {},
            onFavoriteToggle = {},
            onProfileClick = {}
        )
    }
}