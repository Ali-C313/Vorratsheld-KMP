package com.example.vorratsheld.ui.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.data.repository.AuthRepositoryMock
import com.example.vorratsheld.data.repository.ShoppingRepositoryMock
import com.example.vorratsheld.data.repository.UserRepositoryMock
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.shopping_done_section
import com.example.vorratsheld.ui.auth.AuthViewModel
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// Einkaufsliste des eingeloggten Nutzers, offen und erledigt getrennt
@Composable
fun ShoppingListScreen(
    navController: NavHostController,
    viewModel: ShoppingListViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    // Immer nur eine Zeile gleichzeitig aufgewischt
    var openItemId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUser) {
        currentUser?.let { user -> viewModel.load(user.uid) }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0),
        topBar = {
            ShoppingHeader(
                doneCount = uiState.doneItems.size,
                totalCount = uiState.items.size,
                onBackClick = { navController.popBackStack() },
                onClearDone = viewModel::clearDone
            )
        },
        bottomBar = { AddItemBar(onAddItem = viewModel::addItem) }
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
                        onToggle = { viewModel.toggleChecked(item) },
                        onQuantityChange = { delta -> viewModel.changeQuantity(item, delta) },
                        onDelete = {
                            openItemId = null
                            viewModel.delete(item)
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
                            onToggle = { viewModel.toggleChecked(item) },
                            onQuantityChange = { delta -> viewModel.changeQuantity(item, delta) },
                            onDelete = {
                                openItemId = null
                                viewModel.delete(item)
                            },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

// Preview mit den Mock-Repositories statt Firebase
@Suppress("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun ShoppingListScreenPreview() {
    VorratsheldTheme {
        ShoppingListScreen(
            navController = rememberNavController(),
            viewModel = remember {
                ShoppingListViewModel(ShoppingRepositoryMock()).apply { load(AuthRepositoryMock.MOCK_USER_ID) }
            },
            authViewModel = remember { AuthViewModel(AuthRepositoryMock(), UserRepositoryMock()) }
        )
    }
}
