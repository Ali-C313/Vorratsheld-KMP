package com.example.vorratsheld.ui.productdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.data.enums.StorageLocation
import com.example.vorratsheld.data.repository.AuthRepositoryMock
import com.example.vorratsheld.data.repository.OpenFoodFactsRepositoryMock
import com.example.vorratsheld.data.repository.ProductRepositoryMock
import com.example.vorratsheld.data.repository.ShoppingRepositoryMock
import com.example.vorratsheld.data.repository.UserRepositoryMock
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_add
import com.example.vorratsheld.resources.action_apply
import com.example.vorratsheld.resources.action_back
import com.example.vorratsheld.resources.action_cancel
import com.example.vorratsheld.resources.action_delete
import com.example.vorratsheld.resources.action_save
import com.example.vorratsheld.resources.expiry_scan_button
import com.example.vorratsheld.resources.favorite
import com.example.vorratsheld.resources.product_delete_text
import com.example.vorratsheld.resources.product_delete_title
import com.example.vorratsheld.resources.product_edit_title
import com.example.vorratsheld.resources.product_expiry
import com.example.vorratsheld.resources.product_favorite_hint
import com.example.vorratsheld.resources.product_location
import com.example.vorratsheld.resources.product_name
import com.example.vorratsheld.resources.product_new_title
import com.example.vorratsheld.resources.product_quantity
import com.example.vorratsheld.resources.storage_fridge
import com.example.vorratsheld.resources.storage_pantry
import com.example.vorratsheld.ui.auth.AuthViewModel
import com.example.vorratsheld.ui.navigation.Screen
import com.example.vorratsheld.ui.theme.FavoriteYellow
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import com.example.vorratsheld.util.DateHelper
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// Produkt nach dem Scan anlegen oder ein bestehendes bearbeiten
@OptIn(ExperimentalMaterial3Api::class)
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

    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Nach einem Barcode-Scan direkt mit dem MHD weitermachen
    var showExpiryScan by rememberSaveable { mutableStateOf(barcode.isNotEmpty()) }

    LaunchedEffect(Unit) {
        when {
            productId.isNotEmpty() -> viewModel.loadForEdit(productId)
            barcode.isNotEmpty() -> viewModel.loadForNewScan(barcode)
        }
    }

    if (showExpiryScan) {
        ExpiryScanOverlay(
            onDateConfirmed = { date ->
                viewModel.updateExpiryDate(date)
                showExpiryScan = false
            },
            onManualPick = {
                showExpiryScan = false
                showDatePicker = true
            },
            onClose = { showExpiryScan = false }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (uiState.isEditMode) Res.string.product_edit_title else Res.string.product_new_title))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back))
                    }
                },
                actions = {
                    if (uiState.isEditMode) {
                        IconButton(onClick = { showDeleteConfirmation = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(Res.string.action_delete))
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text(stringResource(Res.string.product_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Column {
                Text(text = stringResource(Res.string.product_location), style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    FilterChip(
                        selected = uiState.location == StorageLocation.KUEHLSCHRANK,
                        onClick = { viewModel.updateLocation(StorageLocation.KUEHLSCHRANK) },
                        label = { Text(stringResource(Res.string.storage_fridge)) }
                    )
                    FilterChip(
                        selected = uiState.location == StorageLocation.KAMMER,
                        onClick = { viewModel.updateLocation(StorageLocation.KAMMER) },
                        label = { Text(stringResource(Res.string.storage_pantry)) }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.product_quantity),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f)
                )
                FilledTonalIconButton(
                    onClick = { viewModel.changeQuantity(-1) },
                    enabled = uiState.quantity > 1
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = null)
                }
                Text(
                    text = uiState.quantity.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                FilledTonalIconButton(onClick = { viewModel.changeQuantity(1) }) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }

            Column {
                Text(text = stringResource(Res.string.product_expiry), style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(DateHelper.formatDate(uiState.expiryDate), modifier = Modifier.padding(start = 8.dp))
                    }
                    FilledTonalButton(
                        onClick = { showExpiryScan = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.DocumentScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(stringResource(Res.string.expiry_scan_button), modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = viewModel::toggleFavorite) {
                    Icon(
                        imageVector = if (uiState.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = stringResource(Res.string.favorite),
                        tint = if (uiState.isFavorite) FavoriteYellow else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = stringResource(Res.string.product_favorite_hint),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            uiState.errorMessage?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    currentUser?.let { user ->
                        viewModel.save(user.uid) { navController.popBackStack() }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(if (uiState.isEditMode) Res.string.action_save else Res.string.action_add))
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.expiryDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.updateExpiryDate(it) }
                    showDatePicker = false
                }) {
                    Text(stringResource(Res.string.action_apply))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(Res.string.product_delete_title)) },
            text = { Text(stringResource(Res.string.product_delete_text, uiState.name)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirmation = false
                    currentUser?.let { user ->
                        // Die Produktansicht darunter gibt es nicht mehr, deshalb bis zur Übersicht zurück
                        viewModel.delete(user.uid) { navController.popBackStack(Screen.Overview, inclusive = false) }
                    }
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
