package com.example.vorratsheld.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.data.enums.AppLanguage
import com.example.vorratsheld.data.repository.AuthRepositoryMock
import com.example.vorratsheld.data.repository.ProductRepositoryMock
import com.example.vorratsheld.data.repository.ShoppingRepositoryMock
import com.example.vorratsheld.data.repository.UserRepositoryMock
import com.example.vorratsheld.data.settings.AppLockSettings
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.language_system
import com.example.vorratsheld.ui.locale.customAppLocale
import com.example.vorratsheld.ui.navigation.Screen
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import com.example.vorratsheld.ui.viewmodel.AuthViewModel
import com.example.vorratsheld.ui.viewmodel.ProfileViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// Stateful: Profil laden, Navigation, Abmelden
@Composable
fun SettingsScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()
    var appLockEnabled by remember { mutableStateOf(AppLockSettings.enabled) }

    // Nach "Profil bearbeiten" bzw. "App-Sperre" die Anzeige aktualisieren
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        profileViewModel.loadProfile()
        appLockEnabled = AppLockSettings.enabled
    }

    val currentLanguage = AppLanguage.entries.firstOrNull { it.code == customAppLocale }?.nativeName
        ?: stringResource(Res.string.language_system)

    SettingsContent(
        profileState = profileState,
        currentLanguage = currentLanguage,
        appLockEnabled = appLockEnabled,
        onAppLockClick = { navController.navigate(Screen.SecuritySettings) },
        onBackClick = { navController.popBackStack() },
        onProfileClick = { navController.navigate(Screen.EditProfile) },
        onLanguageClick = { navController.navigate(Screen.Language) },
        onHelpClick = { navController.navigate(Screen.Help) },
        onLegalClick = { type -> navController.navigate(Screen.Legal(type.name)) },
        onLogoutConfirmed = {
            authViewModel.logout {
                // Ganzen Backstack leeren, damit "Zurück" nicht in die App führt
                navController.navigate(Screen.Login) {
                    popUpTo(Screen.Overview) { inclusive = true }
                }
            }
        }
    )
}

// Preview mit den Mock-Repositories statt Firebase
@Suppress("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    VorratsheldTheme {
        SettingsScreen(
            navController = rememberNavController(),
            authViewModel = remember { AuthViewModel(AuthRepositoryMock(), UserRepositoryMock()) },
            profileViewModel = remember { ProfileViewModel(AuthRepositoryMock(), UserRepositoryMock(), ProductRepositoryMock(), ShoppingRepositoryMock()) }
        )
    }
}
