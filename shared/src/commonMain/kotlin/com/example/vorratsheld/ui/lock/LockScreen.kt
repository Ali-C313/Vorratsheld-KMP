package com.example.vorratsheld.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vorratsheld.data.enums.BiometricResult
import com.example.vorratsheld.data.enums.BiometricType
import com.example.vorratsheld.platform.PlatformBackHandler
import com.example.vorratsheld.platform.rememberBiometricAuthenticator
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_cancel
import com.example.vorratsheld.resources.lock_biometric_reason
import com.example.vorratsheld.resources.lock_enter_pin
import com.example.vorratsheld.resources.lock_forgot_pin
import com.example.vorratsheld.resources.lock_forgot_text
import com.example.vorratsheld.resources.lock_title
import com.example.vorratsheld.resources.lock_use_pin
import com.example.vorratsheld.resources.lock_wait
import com.example.vorratsheld.resources.lock_wrong_pin
import com.example.vorratsheld.resources.settings_logout
import com.example.vorratsheld.ui.applock.AppLockViewModel
import com.example.vorratsheld.ui.components.PadIconKey
import com.example.vorratsheld.ui.components.PinPad
import com.example.vorratsheld.ui.components.VorratsheldLogo
import com.example.vorratsheld.ui.theme.Cream
import com.example.vorratsheld.ui.theme.GreenDark
import com.example.vorratsheld.ui.theme.LockGradient
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import com.example.vorratsheld.util.BiometricHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val ATTEMPTS_BEFORE_WAIT = 5

// Liegt über der ganzen App, bis Face ID, Fingerabdruck oder der PIN stimmt
@Composable
fun LockScreen(
    onUnlocked: () -> Unit,
    onForgotPin: () -> Unit,
    viewModel: AppLockViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authenticator = rememberBiometricAuthenticator()
    val biometricType = remember(uiState.biometricsEnabled) {
        if (uiState.biometricsEnabled) authenticator.type else BiometricType.NONE
    }
    val scope = rememberCoroutineScope()

    var entered by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    var shakeTrigger by remember { mutableIntStateOf(0) }
    var failedAttempts by remember { mutableIntStateOf(0) }
    var waitSeconds by remember { mutableIntStateOf(0) }
    var checking by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }

    val reason = stringResource(Res.string.lock_biometric_reason)
    val usePin = stringResource(Res.string.lock_use_pin)
    val wrongPin = stringResource(Res.string.lock_wrong_pin)

    fun askBiometric() {
        scope.launch {
            if (authenticator.authenticate(reason, usePin) == BiometricResult.SUCCESS) onUnlocked()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    // Face ID oder Fingerabdruck gleich beim Erscheinen anbieten
    LaunchedEffect(biometricType) {
        if (biometricType != BiometricType.NONE) askBiometric()
    }

    // Nach zu vielen Fehlversuchen herunterzählen
    LaunchedEffect(waitSeconds) {
        if (waitSeconds > 0) {
            delay(1000)
            waitSeconds -= 1
        }
    }

    // Zurück darf die Sperre nicht umgehen
    PlatformBackHandler(onBack = {})

    fun onDigit(digit: Char) {
        if (checking || entered.length >= viewModel.pinLength) return
        entered += digit
        errorText = null
        if (entered.length == viewModel.pinLength) {
            val pin = entered
            checking = true
            viewModel.checkPin(pin) { correct ->
                checking = false
                if (correct) {
                    onUnlocked()
                } else {
                    failedAttempts += 1
                    shakeTrigger += 1
                    entered = ""
                    errorText = wrongPin
                    if (failedAttempts % ATTEMPTS_BEFORE_WAIT == 0) {
                        // Jede weitere Runde Fehlversuche wartet länger
                        waitSeconds = 30 * (failedAttempts / ATTEMPTS_BEFORE_WAIT)
                    }
                }
            }
        }
    }

    // Surface fängt alle Berührungen ab, darunter bleibt nichts bedienbar
    Surface(modifier = Modifier.fillMaxSize(), color = GreenDark) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(LockGradient)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(vertical = 24.dp)
            ) {
                VorratsheldLogo(modifier = Modifier.size(96.dp))
                Spacer(Modifier.height(20.dp))

                PinPad(
                    title = stringResource(Res.string.lock_title),
                    subtitle = stringResource(Res.string.lock_enter_pin),
                    entered = entered,
                    onDigit = ::onDigit,
                    onDelete = {
                        entered = entered.dropLast(1)
                        errorText = null
                    },
                    errorText = if (waitSeconds > 0) stringResource(Res.string.lock_wait, waitSeconds) else errorText,
                    shakeTrigger = shakeTrigger,
                    enabled = waitSeconds == 0 && !checking,
                    extraKey = if (biometricType != BiometricType.NONE) {
                        {
                            PadIconKey(
                                icon = BiometricHelper.icon(biometricType),
                                description = BiometricHelper.label(biometricType),
                                onClick = ::askBiometric
                            )
                        }
                    } else {
                        null
                    }
                )

                TextButton(
                    onClick = { showForgotDialog = true },
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(stringResource(Res.string.lock_forgot_pin), color = Cream)
                }
            }
        }
    }

    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text(stringResource(Res.string.lock_forgot_pin)) },
            text = { Text(stringResource(Res.string.lock_forgot_text)) },
            confirmButton = {
                TextButton(onClick = {
                    showForgotDialog = false
                    onForgotPin()
                }) {
                    Text(stringResource(Res.string.settings_logout), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }
}

@Suppress("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun LockScreenPreview() {
    VorratsheldTheme {
        LockScreen(
            onUnlocked = {},
            onForgotPin = {},
            viewModel = remember { AppLockViewModel() }
        )
    }
}
