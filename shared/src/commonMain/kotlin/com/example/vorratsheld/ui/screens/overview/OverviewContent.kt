package com.example.vorratsheld.ui.screens.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.data.enums.ExpiryUrgency
import com.example.vorratsheld.data.enums.StorageLocation
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_delete
import com.example.vorratsheld.resources.action_edit
import com.example.vorratsheld.resources.action_undo
import com.example.vorratsheld.resources.overview_favorite_updated
import com.example.vorratsheld.resources.overview_scan
import com.example.vorratsheld.resources.product_deleted
import com.example.vorratsheld.resources.product_deleted_to_list
import com.example.vorratsheld.resources.storage_fridge
import com.example.vorratsheld.resources.storage_pantry
import com.example.vorratsheld.ui.components.ProductCard
import com.example.vorratsheld.ui.components.SwipeActionButton
import com.example.vorratsheld.ui.components.SwipeRevealBox
import com.example.vorratsheld.util.DateHelper
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

// Stateless: reine UI, unabhängig vom ViewModel – dadurch previewbar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OverviewContent(
    products: List<Product>,
    firstName: String,
    fullName: String,
    photoBase64: String?,
    errorMessage: String?,
    onScanClick: () -> Unit,
    onShoppingListClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onFavoriteToggle: (Product) -> Unit,
    onSettingsClick: () -> Unit,
    onEditClick: (Product) -> Unit = {},
    onDeleteProduct: (Product) -> Unit = {},
    onRestoreProduct: (Product) -> Unit = {}
) {
    var selectedLocation by remember { mutableStateOf(StorageLocation.KUEHLSCHRANK) }
    // Immer nur eine Karte gleichzeitig aufgewischt
    var openProductId by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(errorMessage) {
        errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    val filteredProducts = products
        .filter { it.location == selectedLocation }
        .sortedBy { it.expiryDate }

    val urgencies = products.map { DateHelper.expiryUrgencyFor(it.expiryDate) }
    val expiredCount = urgencies.count { it == ExpiryUrgency.ABGELAUFEN }
    val soonCount = urgencies.count { it == ExpiryUrgency.BALD_ABLAUFEND }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onScanClick,
                icon = { Icon(Icons.Filled.QrCodeScanner, contentDescription = null) },
                text = { Text(stringResource(Res.string.overview_scan)) },
                shape = RoundedCornerShape(20.dp)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            // Oben zeichnet der Header selbst hinter die Statusleiste, unten halten wir Abstand zur Navigationsleiste
            contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding() + 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                OverviewHeader(
                    firstName = firstName,
                    fullName = fullName,
                    photoBase64 = photoBase64,
                    expiredCount = expiredCount,
                    soonCount = soonCount,
                    totalCount = products.size,
                    onShoppingListClick = onShoppingListClick,
                    onSettingsClick = onSettingsClick
                )
            }

            item {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    StorageLocation.entries.forEachIndexed { index, location ->
                        SegmentedButton(
                            selected = selectedLocation == location,
                            onClick = { selectedLocation = location },
                            shape = SegmentedButtonDefaults.itemShape(index, StorageLocation.entries.size),
                            icon = {
                                Icon(
                                    if (location == StorageLocation.KUEHLSCHRANK) Icons.Filled.Kitchen else Icons.Filled.Inventory2,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        ) {
                            Text(
                                stringResource(
                                    if (location == StorageLocation.KUEHLSCHRANK) Res.string.storage_fridge else Res.string.storage_pantry
                                )
                            )
                        }
                    }
                }
            }

            if (filteredProducts.isEmpty()) {
                item { OverviewEmptyState(onScanClick = onScanClick) }
            } else {
                items(filteredProducts, key = { it.id }) { product ->
                    SwipeRevealBox(
                        isOpen = openProductId == product.id,
                        onOpenChange = { open -> openProductId = if (open) product.id else null },
                        actionsWidth = 160.dp,
                        actions = {
                            SwipeActionButton(
                                icon = Icons.Filled.Edit,
                                label = stringResource(Res.string.action_edit),
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                onClick = {
                                    openProductId = null
                                    onEditClick(product)
                                }
                            )
                            SwipeActionButton(
                                icon = Icons.Filled.Delete,
                                label = stringResource(Res.string.action_delete),
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                onClick = {
                                    openProductId = null
                                    onDeleteProduct(product)
                                    coroutineScope.launch {
                                        val message = if (product.isFavorite) {
                                            getString(Res.string.product_deleted_to_list, product.name)
                                        } else {
                                            getString(Res.string.product_deleted, product.name)
                                        }
                                        val result = snackbarHostState.showSnackbar(
                                            message = message,
                                            actionLabel = getString(Res.string.action_undo),
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) onRestoreProduct(product)
                                    }
                                }
                            )
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .animateItem()
                    ) {
                        ProductCard(
                            product = product,
                            onClick = {
                                // Offene Karte erst zuklappen statt direkt zu öffnen
                                if (openProductId != null) openProductId = null else onProductClick(product)
                            },
                            onFavoriteToggle = {
                                onFavoriteToggle(product)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(getString(Res.string.overview_favorite_updated, product.name))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
