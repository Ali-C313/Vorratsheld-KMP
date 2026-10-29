package com.example.vorratsheld.ui.editprofile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.data.repository.AuthRepositoryMock
import com.example.vorratsheld.data.repository.ProductRepositoryMock
import com.example.vorratsheld.data.repository.ShoppingRepositoryMock
import com.example.vorratsheld.data.repository.UserRepositoryMock
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_back
import com.example.vorratsheld.resources.action_cancel
import com.example.vorratsheld.resources.action_save
import com.example.vorratsheld.resources.delete_account_text
import com.example.vorratsheld.resources.delete_account_title
import com.example.vorratsheld.resources.email
import com.example.vorratsheld.resources.error_password_too_short
import com.example.vorratsheld.resources.error_passwords_mismatch
import com.example.vorratsheld.resources.error_photo_failed
import com.example.vorratsheld.resources.password
import com.example.vorratsheld.resources.profile_change_password_button
import com.example.vorratsheld.resources.profile_change_photo
import com.example.vorratsheld.resources.profile_current_password
import com.example.vorratsheld.resources.profile_current_password_hint
import com.example.vorratsheld.resources.profile_delete_account
import com.example.vorratsheld.resources.profile_email_hint
import com.example.vorratsheld.resources.profile_first_name
import com.example.vorratsheld.resources.profile_last_name
import com.example.vorratsheld.resources.profile_new_password
import com.example.vorratsheld.resources.profile_new_password_confirm
import com.example.vorratsheld.resources.profile_remove_photo
import com.example.vorratsheld.resources.profile_section_password
import com.example.vorratsheld.resources.profile_title
import com.example.vorratsheld.ui.components.ProfileAvatar
import com.example.vorratsheld.ui.navigation.Screen
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import com.example.vorratsheld.util.AppLock
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// Profil bearbeiten: Foto, Name, E-Mail, Passwort und Konto löschen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var emailPassword by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val photoFailedText = stringResource(Res.string.error_photo_failed)
    val tooShortText = stringResource(Res.string.error_password_too_short)
    val mismatchText = stringResource(Res.string.error_passwords_mismatch)

    // Bild auswählen und auf 256 px verkleinern, damit es in Firestore passt.
    // In der Preview gibt es keine Bildauswahl.
    val photoPicker = if (LocalInspectionMode.current) null else rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        if (file != null) {
            scope.launch {
                try {
                    val bytes = FileKit.compressImage(file = file, quality = 80, maxWidth = 256, maxHeight = 256)
                    viewModel.setPhoto(bytes)
                } catch (e: Exception) {
                    viewModel.showError(photoFailedText)
                }
            }
        }
    }

    val pickPhoto: () -> Unit = {
        // Die Fotoauswahl verlässt kurz die App, dabei nicht sperren
        AppLock.skipLockOnce()
        photoPicker?.launch()
    }

    // Erfolg und Fehler aus dem ViewModel als Snackbar zeigen
    LaunchedEffect(uiState.message, uiState.errorMessage) {
        val text = uiState.message ?: uiState.errorMessage
        if (text != null) {
            snackbarHostState.showSnackbar(text)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.profile_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back))
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Foto
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileAvatar(
                    photoBase64 = uiState.photoBase64,
                    name = uiState.fullName,
                    size = 112.dp,
                    modifier = Modifier.clickable { pickPhoto() }
                )
                Row {
                    TextButton(onClick = { pickPhoto() }) {
                        Text(stringResource(Res.string.profile_change_photo))
                    }
                    if (uiState.photoBase64 != null) {
                        TextButton(onClick = viewModel::removePhoto) {
                            Text(stringResource(Res.string.profile_remove_photo), color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            // Name und E-Mail
            OutlinedTextField(
                value = uiState.firstName,
                onValueChange = viewModel::updateFirstName,
                label = { Text(stringResource(Res.string.profile_first_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = viewModel::updateLastName,
                label = { Text(stringResource(Res.string.profile_last_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.newEmail,
                onValueChange = viewModel::updateEmail,
                label = { Text(stringResource(Res.string.email)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                supportingText = { Text(stringResource(Res.string.profile_email_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            // Passwort nur abfragen, wenn die E-Mail geändert wurde
            if (uiState.emailChanged) {
                OutlinedTextField(
                    value = emailPassword,
                    onValueChange = { emailPassword = it },
                    label = { Text(stringResource(Res.string.profile_current_password)) },
                    supportingText = { Text(stringResource(Res.string.profile_current_password_hint)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = { viewModel.saveProfile(emailPassword) },
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(Res.string.action_save))
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Passwort ändern
            Text(text = stringResource(Res.string.profile_section_password), style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text(stringResource(Res.string.profile_current_password)) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text(stringResource(Res.string.profile_new_password)) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text(stringResource(Res.string.profile_new_password_confirm)) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            passwordError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

            OutlinedButton(
                onClick = {
                    passwordError = when {
                        newPassword.length < 6 -> tooShortText
                        newPassword != confirmPassword -> mismatchText
                        else -> null
                    }
                    if (passwordError == null) {
                        viewModel.changePassword(currentPassword, newPassword) {
                            currentPassword = ""
                            newPassword = ""
                            confirmPassword = ""
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.profile_change_password_button))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Konto löschen, verlangen Apple und Google bei Apps mit Registrierung
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.profile_delete_account))
            }
        }
    }

    if (showDeleteDialog) {
        var deletePassword by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(Res.string.delete_account_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(Res.string.delete_account_text))
                    OutlinedTextField(
                        value = deletePassword,
                        onValueChange = { deletePassword = it },
                        label = { Text(stringResource(Res.string.password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteAccount(deletePassword) {
                            navController.navigate(Screen.Login) {
                                popUpTo(Screen.Overview) { inclusive = true }
                            }
                        }
                    },
                    enabled = deletePassword.isNotBlank()
                ) {
                    Text(stringResource(Res.string.profile_delete_account), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
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
private fun EditProfileScreenPreview() {
    VorratsheldTheme {
        EditProfileScreen(
            navController = rememberNavController(),
            viewModel = remember {
                ProfileViewModel(AuthRepositoryMock(), UserRepositoryMock(), ProductRepositoryMock(), ShoppingRepositoryMock())
            }
        )
    }
}
