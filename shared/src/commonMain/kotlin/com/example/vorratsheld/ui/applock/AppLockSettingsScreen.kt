package com.example.vorratsheld.ui.applock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.vorratsheld.data.enums.BiometricResult
import com.example.vorratsheld.data.enums.BiometricType
import com.example.vorratsheld.data.enums.LockTimeout
import com.example.vorratsheld.data.enums.PinFlowMode
import com.example.vorratsheld.platform.rememberBiometricAuthenticator
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_back
import com.example.vorratsheld.resources.action_cancel
import com.example.vorratsheld.resources.applock_change_pin
import com.example.vorratsheld.resources.applock_enable
import com.example.vorratsheld.resources.applock_enable_hint
import com.example.vorratsheld.resources.applock_intro
import com.example.vorratsheld.resources.applock_no_biometrics
import com.example.vorratsheld.resources.applock_timeout
import com.example.vorratsheld.resources.applock_title
import com.example.vorratsheld.resources.applock_unlock_with
import com.example.vorratsheld.resources.lock_biometric_reason
import com.example.vorratsheld.resources.lock_use_pin
import com.example.vorratsheld.ui.components.SettingsItem
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import com.example.vorratsheld.util.BiometricHelper
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// Einstellungen der App-Sperre: an/aus, Face ID oder Fingerabdruck, Sperrzeit, PIN ändern
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLockSettingsScreen(
    navController: NavHostController,
    viewModel: AppLockViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authenticator = rememberBiometricAuthenticator()
    val biometricType = remember { authenticator.type }
    val scope = rememberCoroutineScope()

    var pinFlow by remember { mutableStateOf<PinFlowMode?>(null) }
    var showTimeoutDialog by remember { mutableStateOf(false) }

    val reason = stringResource(Res.string.lock_biometric_reason)
    val usePin = stringResource(Res.string.lock_use_pin)

    pinFlow?.let { mode ->
        PinFlowScreen(
            mode = mode,
            onFinished = {
                when (mode) {
                    // Wer Face ID oder Fingerabdruck hat, will es meistens auch nutzen
                    PinFlowMode.CREATE -> if (biometricType != BiometricType.NONE) viewModel.setBiometricsEnabled(true)
                    PinFlowMode.DISABLE -> viewModel.disable()
                    PinFlowMode.CHANGE -> Unit
                }
                pinFlow = null
            },
            onCancel = { pinFlow = null },
            viewModel = viewModel
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.applock_title)) },
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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
                    .size(88.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            ) {
                Icon(
                    imageVector = if (biometricType != BiometricType.NONE) BiometricHelper.icon(biometricType) else Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(44.dp)
                )
            }
            Text(
                text = stringResource(Res.string.applock_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                SwitchRow(
                    icon = Icons.Filled.Lock,
                    title = stringResource(Res.string.applock_enable),
                    subtitle = stringResource(Res.string.applock_enable_hint),
                    checked = uiState.enabled,
                    onCheckedChange = { on -> pinFlow = if (on) PinFlowMode.CREATE else PinFlowMode.DISABLE }
                )
            }

            if (uiState.enabled) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    if (biometricType != BiometricType.NONE) {
                        SwitchRow(
                            icon = BiometricHelper.icon(biometricType),
                            title = stringResource(Res.string.applock_unlock_with, BiometricHelper.label(biometricType)),
                            subtitle = null,
                            checked = uiState.biometricsEnabled,
                            onCheckedChange = { on ->
                                if (on) {
                                    // Erst einmal bestätigen lassen, dass es wirklich klappt
                                    scope.launch {
                                        if (authenticator.authenticate(reason, usePin) == BiometricResult.SUCCESS) {
                                            viewModel.setBiometricsEnabled(true)
                                        }
                                    }
                                } else {
                                    viewModel.setBiometricsEnabled(false)
                                }
                            }
                        )
                    } else {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(Res.string.applock_no_biometrics),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        )
                    }
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Filled.Timer,
                        title = stringResource(Res.string.applock_timeout),
                        subtitle = timeoutLabel(uiState.timeout),
                        onClick = { showTimeoutDialog = true }
                    )
                    HorizontalDivider()
                    SettingsItem(
                        icon = Icons.Filled.Password,
                        title = stringResource(Res.string.applock_change_pin),
                        onClick = { pinFlow = PinFlowMode.CHANGE }
                    )
                }
            }
        }
    }

    if (showTimeoutDialog) {
        AlertDialog(
            onDismissRequest = { showTimeoutDialog = false },
            title = { Text(stringResource(Res.string.applock_timeout)) },
            text = {
                Column {
                    LockTimeout.entries.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = option == uiState.timeout,
                                    role = Role.RadioButton,
                                    onClick = {
                                        viewModel.setTimeout(option)
                                        showTimeoutDialog = false
                                    }
                                )
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = option == uiState.timeout, onClick = null)
                            Text(text = timeoutLabel(option), modifier = Modifier.padding(start = 12.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTimeoutDialog = false }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }
}

@Suppress("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun AppLockSettingsScreenPreview() {
    VorratsheldTheme {
        AppLockSettingsScreen(
            navController = rememberNavController(),
            viewModel = remember { AppLockViewModel() }
        )
    }
}
