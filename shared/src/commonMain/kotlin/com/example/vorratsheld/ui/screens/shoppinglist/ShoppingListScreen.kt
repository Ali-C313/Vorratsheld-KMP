package com.example.vorratsheld.ui.screens.shoppinglist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.data.repository.AuthRepositoryMock
import com.example.vorratsheld.data.repository.ShoppingRepositoryMock
import com.example.vorratsheld.data.repository.UserRepositoryMock
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import com.example.vorratsheld.ui.viewmodel.AuthViewModel
import com.example.vorratsheld.ui.viewmodel.ShoppingListViewModel
import org.koin.compose.viewmodel.koinViewModel

// Stateful: lädt die Einkaufsliste des eingeloggten Nutzers
@Composable
fun ShoppingListScreen(
    navController: NavHostController,
    viewModel: ShoppingListViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(currentUser) {
        currentUser?.let { user -> viewModel.load(user.uid) }
    }

    ShoppingListContent(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onAddItem = viewModel::addItem,
        onToggle = viewModel::toggleChecked,
        onQuantityChange = viewModel::changeQuantity,
        onDelete = viewModel::delete,
        onClearDone = viewModel::clearDone,
        onErrorShown = viewModel::clearError
    )
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
