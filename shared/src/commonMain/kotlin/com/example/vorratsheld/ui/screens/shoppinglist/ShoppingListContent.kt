package com.example.vorratsheld.ui.screens.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.data.model.ShoppingItem
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.shopping_done_section
import com.example.vorratsheld.ui.state.ShoppingListUiState
import org.jetbrains.compose.resources.stringResource

// Stateless: reine UI – dadurch previewbar
@Composable
internal fun ShoppingListContent(
    uiState: ShoppingListUiState,
    onBackClick: () -> Unit,
    onAddItem: (String) -> Unit,
    onToggle: (ShoppingItem) -> Unit,
    onQuantityChange: (ShoppingItem, Int) -> Unit,
    onDelete: (ShoppingItem) -> Unit,
    onClearDone: () -> Unit,
    onErrorShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var openItemId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onErrorShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0),
        topBar = {
            ShoppingHeader(
                doneCount = uiState.doneItems.size,
                totalCount = uiState.items.size,
                onBackClick = onBackClick,
                onClearDone = onClearDone
            )
        },
        bottomBar = { AddItemBar(onAddItem = onAddItem) }
    ) { innerPadding ->
        if (uiState.items.isEmpty() && !uiState.isLoading) {
            EmptyShoppingList(modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    bottom = innerPadding.calculateBottomPadding() + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.openItems, key = { it.id }) { item ->
                    SwipeableShoppingRow(
                        item = item,
                        isOpen = openItemId == item.id,
                        onOpenChange = { open -> openItemId = if (open) item.id else null },
                        onToggle = { onToggle(item) },
                        onQuantityChange = { delta -> onQuantityChange(item, delta) },
                        onDelete = {
                            openItemId = null
                            onDelete(item)
                        },
                        modifier = Modifier.animateItem()
                    )
                }

                if (uiState.doneItems.isNotEmpty()) {
                    item(key = "done_header") {
                        Text(
                            text = "${stringResource(Res.string.shopping_done_section)} (${uiState.doneItems.size})",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 4.dp)
                                .animateItem()
                        )
                    }
                    items(uiState.doneItems, key = { it.id }) { item ->
                        SwipeableShoppingRow(
                            item = item,
                            isOpen = openItemId == item.id,
                            onOpenChange = { open -> openItemId = if (open) item.id else null },
                            onToggle = { onToggle(item) },
                            onQuantityChange = { delta -> onQuantityChange(item, delta) },
                            onDelete = {
                                openItemId = null
                                onDelete(item)
                            },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}
