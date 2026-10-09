package com.example.vorratsheld.ui.screens.productinfo

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.data.model.Product
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_back
import com.example.vorratsheld.resources.action_cancel
import com.example.vorratsheld.resources.action_delete
import com.example.vorratsheld.resources.error_product_not_found
import com.example.vorratsheld.resources.product_delete_text
import com.example.vorratsheld.resources.product_delete_title
import org.jetbrains.compose.resources.stringResource

// Stateless: reine UI – dadurch previewbar
@Composable
internal fun ProductInfoContent(
    product: Product?,
    isLoading: Boolean,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onAddToListClick: () -> Unit,
    onDeleteConfirmed: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        // Header zeichnet selbst hinter die Statusleiste
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (product != null) {
                ActionBar(
                    onDeleteClick = { showDeleteConfirmation = true },
                    onAddToListClick = onAddToListClick,
                    onEditClick = onEditClick
                )
            }
        }
    ) { innerPadding ->
        when {
            product == null && isLoading -> {
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
                    TextButton(onClick = onBackClick) { Text(stringResource(Res.string.action_back)) }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .verticalScroll(rememberScrollState())
                ) {
                    InfoHeader(product = product, onBackClick = onBackClick, onFavoriteToggle = onFavoriteToggle)
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
                    onDeleteConfirmed()
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
