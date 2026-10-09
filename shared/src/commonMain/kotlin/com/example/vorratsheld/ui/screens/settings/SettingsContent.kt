package com.example.vorratsheld.ui.screens.settings

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
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.data.enums.LegalType
import com.example.vorratsheld.platform.APP_VERSION
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_back
import com.example.vorratsheld.resources.action_cancel
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
import com.example.vorratsheld.ui.components.SettingsItem
import com.example.vorratsheld.ui.components.SettingsSectionTitle
import com.example.vorratsheld.ui.state.ProfileUiState
import org.jetbrains.compose.resources.stringResource

// Stateless: reine UI – dadurch previewbar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsContent(
    profileState: ProfileUiState,
    currentLanguage: String,
    appLockEnabled: Boolean = false,
    onAppLockClick: () -> Unit = {},
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLegalClick: (LegalType) -> Unit,
    onLogoutConfirmed: () -> Unit
) {
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
            ProfileCard(state = profileState, onClick = onProfileClick)

            SettingsSectionTitle(
                text = stringResource(Res.string.settings_section_app),
                modifier = Modifier.padding(top = 8.dp)
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                SettingsItem(
                    icon = Icons.Filled.Language,
                    title = stringResource(Res.string.settings_language),
                    subtitle = currentLanguage,
                    onClick = onLanguageClick
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
                    subtitle = stringResource(if (appLockEnabled) Res.string.state_on else Res.string.state_off),
                    onClick = onAppLockClick
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
                    onClick = onHelpClick
                )
                HorizontalDivider()
                SettingsItem(
                    icon = Icons.Filled.Description,
                    title = stringResource(Res.string.settings_terms),
                    onClick = { onLegalClick(LegalType.TERMS) }
                )
                HorizontalDivider()
                SettingsItem(
                    icon = Icons.Filled.PrivacyTip,
                    title = stringResource(Res.string.settings_privacy),
                    onClick = { onLegalClick(LegalType.PRIVACY) }
                )
                HorizontalDivider()
                SettingsItem(
                    icon = Icons.Filled.Info,
                    title = stringResource(Res.string.settings_imprint),
                    onClick = { onLegalClick(LegalType.IMPRINT) }
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
                    onLogoutConfirmed()
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
