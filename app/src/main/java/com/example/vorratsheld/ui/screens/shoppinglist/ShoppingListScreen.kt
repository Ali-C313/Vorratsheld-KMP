package com.example.vorratsheld.ui.screens.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.vorratsheld.data.enums.StorageLocation
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.viewmodel.AuthViewModel
import com.example.vorratsheld.viewmodel.ProductViewModel

// Stateful: holt die Favoriten vom ViewModel, kümmert sich um Navigation
@Composable
fun ShoppingListScreen(
    navController: NavHostController,
    productViewModel: ProductViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val products by productViewModel.products.collectAsStateWithLifecycle()
    val errorMessage by productViewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(currentUser) {
        currentUser?.let { user -> productViewModel.loadProducts(user.uid) }
    }

    val favoriteProducts = products.filter { it.isFavorite }

    ShoppingListContent(
        products = favoriteProducts,
        errorMessage = errorMessage,
        onBoughtToggle = { product ->
            // "Gekauft" abhaken = Favorit zurücksetzen, Produkt verschwindet von der Liste
            currentUser?.let { user -> productViewModel.toggleFavorite(product, user.uid) }
        },
        onBackClick = { navController.popBackStack() }
    )
}

// Stateless: reine UI, unabhängig vom ViewModel – dadurch previewbar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingListContent(
    products: List<Product>,
    errorMessage: String?,
    onBoughtToggle: (Product) -> Unit,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Einkaufsliste") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Noch nichts auf der Einkaufsliste – markiere Produkte in der Übersicht als Favorit.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ShoppingListItem(
                        product = product,
                        onBoughtToggle = { onBoughtToggle(product) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShoppingListItem(product: Product, onBoughtToggle: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(4.dp)
        ) {
            Checkbox(checked = false, onCheckedChange = { onBoughtToggle() })
            androidx.compose.foundation.layout.Column {
                Text(text = product.name, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = if (product.location == StorageLocation.KUEHLSCHRANK) "Kühlschrank" else "Kammer",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShoppingListContentPreview() {
    MaterialTheme {
        ShoppingListContent(
            products = listOf(
                Product(id = "1", name = "Eier (10er Pack)", location = StorageLocation.KUEHLSCHRANK, isFavorite = true),
                Product(id = "2", name = "Mehl", location = StorageLocation.KAMMER, isFavorite = true)
            ),
            errorMessage = null,
            onBoughtToggle = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Leer")
@Composable
private fun ShoppingListContentEmptyPreview() {
    MaterialTheme {
        ShoppingListContent(
            products = emptyList(),
            errorMessage = null,
            onBoughtToggle = {},
            onBackClick = {}
        )
    }
}