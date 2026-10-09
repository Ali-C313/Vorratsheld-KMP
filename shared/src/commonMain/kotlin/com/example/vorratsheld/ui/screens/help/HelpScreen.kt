package com.example.vorratsheld.ui.screens.help

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.data.repository.AuthRepositoryMock
import com.example.vorratsheld.data.repository.SupportRepositoryMock
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import com.example.vorratsheld.ui.viewmodel.SupportViewModel
import org.koin.compose.viewmodel.koinViewModel

// Hilfe-Screen, schickt die Anfrage über das SupportViewModel
@Composable
fun HelpScreen(
    navController: NavHostController,
    viewModel: SupportViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HelpContent(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onCategoryChange = viewModel::updateCategory,
        onSubjectChange = viewModel::updateSubject,
        onMessageChange = viewModel::updateMessage,
        onReplyEmailChange = viewModel::updateReplyEmail,
        onSend = viewModel::send,
        onErrorShown = viewModel::clearError
    )
}

// Preview mit den Mock-Repositories statt Firebase
@Suppress("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun HelpScreenPreview() {
    VorratsheldTheme {
        HelpScreen(
            navController = rememberNavController(),
            viewModel = remember { SupportViewModel(AuthRepositoryMock(), SupportRepositoryMock()) }
        )
    }
}
