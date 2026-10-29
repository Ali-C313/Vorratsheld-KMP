package com.example.vorratsheld.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.data.enums.AppLanguage
import com.example.vorratsheld.data.enums.LegalType
import com.example.vorratsheld.data.repository.AuthRepositoryMock
import com.example.vorratsheld.data.repository.ProductRepositoryMock
import com.example.vorratsheld.data.repository.ShoppingRepositoryMock
import com.example.vorratsheld.data.repository.UserRepositoryMock
import com.example.vorratsheld.platform.APP_VERSION
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_back
import com.example.vorratsheld.resources.action_cancel
import com.example.vorratsheld.resources.language_system
import com.example.vorratsheld.resources.logout_dialog_text
import com.example.vorratsheld.resources.logout_dialog_title
import com.example.vorratsheld.resources.settings_app_lock
import com.example.vorratsheld.resources.settings_help
import com.example.vorratsheld.resources.settings_imprint
import com.example.vorratsheld.resources.settings_language
import com.example.vorratsheld.resources.settings_logout
import com.example.vorratsheld.resources.settings_privacy
import com.example.vorratsheld.resources.settings_section_app
import com.example.vorratsheld.resources.settings_section_security
import com.example.vorratsheld.resources.settings_section_support
import com.example.vorratsheld.resources.settings_terms
import com.example.vorratsheld.resources.settings_title
import com.example.vorratsheld.resources.settings_version
import com.example.vorratsheld.resources.state_off
import com.example.vorratsheld.resources.state_on
import com.example.vorratsheld.ui.applock.AppLockViewModel
import com.example.vorratsheld.ui.auth.AuthViewModel
import com.example.vorratsheld.ui.components.SettingsItem
import com.example.vorratsheld.ui.components.SettingsSectionTitle
import com.example.vorratsheld.ui.editprofile.ProfileViewModel
import com.example.vorratsheld.ui.locale.customAppLocale
import com.example.vorratsheld.ui.navigation.Screen
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// Einstellungen mit Profil, Sprache, App-Sperre, Hilfe und Rechtstexten
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel(),
    appLockViewModel: AppLockViewModel = koinViewModel()
) {
    val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val appLockState by appLockViewModel.uiState.collectAsStateWithLifecycle()
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    // Nach "Profil bearbeiten" oder "App-Sperre" die Anzeige aktualisieren
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        profileViewModel.loadProfile()
        appLockViewModel.refresh()
    }

    val currentLanguage = AppLanguage.entries.firstOrNull { it.code == customAppLocale }?.nativeName
        ?: stringResource(Res.string.language_system)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileCard(state = profileState, onClick = { navController.navigate(Screen.EditProfile) })

            SettingsSectionTitle(
                text = stringResource(Res.string.settings_section_app),
                modifier = Modifier.padding(top = 8.dp)
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                SettingsItem(
                    icon = Icons.Filled.Language,
                    title = stringResource(Res.string.settings_language),
                    subtitle = currentLanguage,
                    onClick = { navController.navigate(Screen.Language) }
                )
            }

            SettingsSectionTitle(
                text = stringResource(Res.string.settings_section_security),
                modifier = Modifier.padding(top = 8.dp)
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                SettingsItem(
                    icon = Icons.Filled.Lock,
                    title = stringResource(Res.string.settings_app_lock),
                    subtitle = stringResource(if (appLockState.enabled) Res.string.state_on else Res.string.state_off),
                    onClick = { navController.navigate(Screen.SecuritySettings) }
                )
            }

            SettingsSectionTitle(
                text = stringResource(Res.string.settings_section_support),
                modifier = Modifier.padding(top = 8.dp)
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    title = stringResource(Res.string.settings_help),
                    onClick = { navController.navigate(Screen.Help) }
                )
                HorizontalDivider()
                SettingsItem(
                    icon = Icons.Filled.Description,
                    title = stringResource(Res.string.settings_terms),
                    onClick = { navController.navigate(Screen.Legal(LegalType.TERMS.name)) }
                )
                HorizontalDivider()
                SettingsItem(
                    icon = Icons.Filled.PrivacyTip,
                    title = stringResource(Res.string.settings_privacy),
                    onClick = { navController.navigate(Screen.Legal(LegalType.PRIVACY.name)) }
                )
                HorizontalDivider()
                SettingsItem(
                    icon = Icons.Filled.Info,
                    title = stringResource(Res.string.settings_imprint),
                    onClick = { navController.navigate(Screen.Legal(LegalType.IMPRINT.name)) }
                )
            }

            OutlinedButton(
                onClick = { showLogoutConfirmation = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(stringResource(Res.string.settings_logout))
            }

            Text(
                text = stringResource(Res.string.settings_version, APP_VERSION),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = { Text(stringResource(Res.string.logout_dialog_title)) },
            text = { Text(stringResource(Res.string.logout_dialog_text)) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutConfirmation = false
                    authViewModel.logout {
                        // Backstack leeren, damit "Zurück" nicht in die App führt
                        navController.navigate(Screen.Login) {
                            popUpTo(Screen.Overview) { inclusive = true }
                        }
                    }
                }) {
                    Text(stringResource(Res.string.settings_logout), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
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
private fun SettingsScreenPreview() {
    VorratsheldTheme {
        SettingsScreen(
            navController = rememberNavController(),
            authViewModel = remember { AuthViewModel(AuthRepositoryMock(), UserRepositoryMock()) },
            profileViewModel = remember {
                ProfileViewModel(AuthRepositoryMock(), UserRepositoryMock(), ProductRepositoryMock(), ShoppingRepositoryMock())
            },
            appLockViewModel = remember { AppLockViewModel() }
        )
    }
}
