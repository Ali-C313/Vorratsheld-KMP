package com.example.vorratsheld.ui.screens.productdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.vorratsheld.data.enums.StorageLocation
import com.example.vorratsheld.viewmodel.AuthViewModel
import com.example.vorratsheld.viewmodel.ProductDetailUiState
import com.example.vorratsheld.viewmodel.ProductDetailViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// Stateful: lädt je nach Modus (Bearbeiten/Neu-Scan) die passenden Daten und kümmert sich um Navigation
@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    productId: String,
    barcode: String,
    viewModel: ProductDetailViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
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
        onNameChange = viewModel::updateName,
        onLocationChange = viewModel::updateLocation,
        onExpiryDateChange = viewModel::updateExpiryDate,
        onFavoriteToggle = viewModel::toggleFavorite,
        onBackClick = { navController.popBackStack() },
        onSaveClick = {
            currentUser?.let { user ->
                viewModel.save(user.uid) { navController.popBackStack() }
            }
        },
        onDeleteClick = {
            viewModel.delete { navController.popBackStack() }
        }
    )
}

// Stateless: reine UI, unabhängig vom ViewModel – dadurch previewbar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailContent(
    uiState: ProductDetailUiState,
    onNameChange: (String) -> Unit,
    onLocationChange: (StorageLocation) -> Unit,
    onExpiryDateChange: (Long) -> Unit,
    onFavoriteToggle: () -> Unit,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Produkt bearbeiten" else "Neues Produkt") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                actions = {
                    if (uiState.isEditMode) {
                        IconButton(onClick = onDeleteClick) {
                            Icon(Icons.Filled.Delete, contentDescription = "Löschen")
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = { Text("Produktname") },
                modifier = Modifier.fillMaxWidth()
            )

            Column {
                Text(text = "Lagerort", style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    FilterChip(
                        selected = uiState.location == StorageLocation.KUEHLSCHRANK,
                        onClick = { onLocationChange(StorageLocation.KUEHLSCHRANK) },
                        label = { Text("Kühlschrank") }
                    )
                    FilterChip(
                        selected = uiState.location == StorageLocation.KAMMER,
                        onClick = { onLocationChange(StorageLocation.KAMMER) },
                        label = { Text("Kammer") }
                    )
                }
            }

            Column {
                Text(text = "Ablaufdatum", style = MaterialTheme.typography.labelMedium)
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(formatDate(uiState.expiryDate))
                }
            }

            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (uiState.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favorit",
                        tint = if (uiState.isFavorite) Color(0xFFFFA000) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Favorit (landet bei Verbrauch automatisch auf der Einkaufsliste)",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            uiState.errorMessage?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.isEditMode) "Speichern" else "Hinzufügen")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.expiryDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onExpiryDateChange(it) }
                    showDatePicker = false
                }) {
                    Text("Übernehmen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Abbrechen")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun formatDate(dateMillis: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)
    return formatter.format(java.util.Date(dateMillis))
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailContentNewPreview() {
    MaterialTheme {
        ProductDetailContent(
            uiState = ProductDetailUiState(
                barcode = "4008400301012",
                name = "Vollmilch 1L",
                location = StorageLocation.KUEHLSCHRANK,
                isEditMode = false
            ),
            onNameChange = {},
            onLocationChange = {},
            onExpiryDateChange = {},
            onFavoriteToggle = {},
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Bearbeiten")
@Composable
private fun ProductDetailContentEditPreview() {
    MaterialTheme {
        ProductDetailContent(
            uiState = ProductDetailUiState(
                productId = "abc123",
                name = "Gouda Scheiben",
                location = StorageLocation.KUEHLSCHRANK,
                isFavorite = true,
                isEditMode = true
            ),
            onNameChange = {},
            onLocationChange = {},
            onExpiryDateChange = {},
            onFavoriteToggle = {},
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {}
        )
    }
}