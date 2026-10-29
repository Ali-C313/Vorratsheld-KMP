package com.example.vorratsheld.ui.productinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.data.repository.ProductRepositoryMock
import com.example.vorratsheld.data.repository.ShoppingRepositoryMock
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_back
import com.example.vorratsheld.resources.action_cancel
import com.example.vorratsheld.resources.action_delete
import com.example.vorratsheld.resources.error_product_not_found
import com.example.vorratsheld.resources.info_added_to_list
import com.example.vorratsheld.resources.product_delete_text
import com.example.vorratsheld.resources.product_delete_title
import com.example.vorratsheld.ui.navigation.Screen
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// Nur-Lesen-Ansicht eines Produkts mit Bearbeiten, Löschen und Einkaufsliste
@Composable
fun ProductInfoScreen(
    navController: NavHostController,
    productId: String,
    viewModel: ProductInfoViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val product = uiState.product
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Bei jedem Betreten neu laden, z. B. nach dem Bearbeiten
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.load(productId)
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        // Der Kopf geht bis unter die Statusleiste
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (product != null) {
                ActionBar(
                    onDeleteClick = { showDeleteConfirmation = true },
                    onAddToListClick = {
                        viewModel.addToShoppingList {
                            scope.launch {
                                snackbarHostState.showSnackbar(getString(Res.string.info_added_to_list, product.name))
                            }
                        }
                    },
                    onEditClick = { navController.navigate(Screen.ProductDetail(productId = productId)) }
                )
            }
        }
    ) { innerPadding ->
        when {
            product == null && uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            product == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(Res.string.error_product_not_found), style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { navController.popBackStack() }) { Text(stringResource(Res.string.action_back)) }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .verticalScroll(rememberScrollState())
                ) {
                    InfoHeader(
                        product = product,
                        onBackClick = { navController.popBackStack() },
                        onFavoriteToggle = viewModel::toggleFavorite
                    )
                    InfoBody(
                        product = product,
                        modifier = Modifier.offset(y = (-28).dp)
                    )
                }
            }
        }
    }

    if (showDeleteConfirmation && product != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(Res.string.product_delete_title)) },
            text = { Text(stringResource(Res.string.product_delete_text, product.name)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirmation = false
                    viewModel.delete { navController.popBackStack() }
                }) {
                    Text(stringResource(Res.string.action_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }
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
