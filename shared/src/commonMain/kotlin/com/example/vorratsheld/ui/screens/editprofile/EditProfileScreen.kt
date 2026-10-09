package com.example.vorratsheld.ui.screens.editprofile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.data.repository.AuthRepositoryMock
import com.example.vorratsheld.data.repository.ProductRepositoryMock
import com.example.vorratsheld.data.repository.ShoppingRepositoryMock
import com.example.vorratsheld.data.repository.UserRepositoryMock
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.error_photo_failed
import com.example.vorratsheld.resources.password
import com.example.vorratsheld.ui.navigation.Screen
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import com.example.vorratsheld.ui.viewmodel.ProfileViewModel
import com.example.vorratsheld.util.AppLock
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// Stateful: Bildauswahl, ViewModel, Navigation nach dem Löschen
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val photoFailedText = stringResource(Res.string.error_photo_failed)

    // Bild auswählen und direkt auf 256 px verkleinern, damit es in Firestore passt
    // In der Preview gibt es keine Bildauswahl
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

    EditProfileContent(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onPickPhoto = {
            // Fotoauswahl verlässt kurz die App, dabei nicht sperren
            AppLock.skipLockOnce()
            photoPicker?.launch()
        },
        onRemovePhoto = viewModel::removePhoto,
        onFirstNameChange = viewModel::updateFirstName,
        onLastNameChange = viewModel::updateLastName,
        onEmailChange = viewModel::updateEmail,
        onSave = viewModel::saveProfile,
        onChangePassword = viewModel::changePassword,
        onDeleteAccount = { password ->
            viewModel.deleteAccount(password) {
                navController.navigate(Screen.Login) {
                    popUpTo(Screen.Overview) { inclusive = true }
                }
            }
        },
        onMessageShown = viewModel::clearMessages
    )
}

// Preview mit den Mock-Repositories statt Firebase
@Suppress("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    VorratsheldTheme {
        EditProfileScreen(
            navController = rememberNavController(),
            viewModel = remember { ProfileViewModel(AuthRepositoryMock(), UserRepositoryMock(), ProductRepositoryMock(), ShoppingRepositoryMock()) }
        )
    }
}
