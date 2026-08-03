package com.example.vorratsheld.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.vorratsheld.ui.navigation.Screen
import com.example.vorratsheld.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

// Stateful: verbindet die reine UI mit dem AuthViewModel und der Navigation
@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val errorMessage by authViewModel.errorMessage.collectAsStateWithLifecycle()

    ProfileContent(
        email = currentUser?.email ?: "",
        errorMessage = errorMessage,
        onChangePassword = { current, new, onDone ->
            authViewModel.changePassword(current, new, onSuccess = onDone)
        },
        onBackClick = { navController.popBackStack() },
        onLogoutConfirmed = {
            authViewModel.logout()
            navController.navigate(Screen.Login.route) {
                popUpTo(0)
            }
        }
    )
}

// Stateless: reine UI, unabhängig vom ViewModel – dadurch previewbar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    email: String,
    errorMessage: String?,
    onChangePassword: (currentPassword: String, newPassword: String, onDone: () -> Unit) -> Unit,
    onBackClick: () -> Unit,
    onLogoutConfirmed: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localValidationError by remember { mutableStateOf<String?>(null) }
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(errorMessage) {
        errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Angemeldet als", style = MaterialTheme.typography.labelMedium)
                    Text(text = email, style = MaterialTheme.typography.bodyLarge)
                }
            }

            HorizontalDivider()

            Text(text = "Passwort ändern", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Aktuelles Passwort") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Neues Passwort") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Neues Passwort bestätigen") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            localValidationError?.let { message ->
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    when {
                        newPassword.length < 6 -> {
                            localValidationError = "Neues Passwort muss mindestens 6 Zeichen haben"
                        }
                        newPassword != confirmPassword -> {
                            localValidationError = "Passwörter stimmen nicht überein"
                        }
                        else -> {
                            localValidationError = null
                            onChangePassword(currentPassword, newPassword) {
                                currentPassword = ""
                                newPassword = ""
                                confirmPassword = ""
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Passwort geändert")
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Passwort ändern")
            }

            HorizontalDivider()

            OutlinedButton(
                onClick = { showLogoutConfirmation = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Abmelden")
            }
        }
    }

    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = { Text("Abmelden?") },
            text = { Text("Du wirst ausgeloggt und zum Login-Screen zurückgebracht.") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutConfirmation = false
                    onLogoutConfirmed()
                }) {
                    Text("Abmelden", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileContentPreview() {
    MaterialTheme {
        ProfileContent(
            email = "ali@example.com",
            errorMessage = null,
            onChangePassword = { _, _, _ -> },
            onBackClick = {},
            onLogoutConfirmed = {}
        )
    }
}