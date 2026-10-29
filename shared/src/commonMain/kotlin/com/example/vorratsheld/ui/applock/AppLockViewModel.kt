package com.example.vorratsheld.ui.applock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vorratsheld.data.enums.LockTimeout
import com.example.vorratsheld.data.settings.AppLockSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// App-Sperre an- und ausschalten, PIN prüfen und speichern
class AppLockViewModel : ViewModel() {

    val pinLength = AppLockSettings.PIN_LENGTH

    private val _uiState = MutableStateFlow(AppLockUiState())
    val uiState: StateFlow<AppLockUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    // Neu einlesen, z. B. nach der Rückkehr aus den Einstellungen
    fun refresh() {
        _uiState.value = AppLockUiState(
            enabled = AppLockSettings.enabled,
            biometricsEnabled = AppLockSettings.biometricsEnabled,
            timeout = AppLockSettings.timeout
        )
    }

    fun setBiometricsEnabled(enabled: Boolean) {
        AppLockSettings.biometricsEnabled = enabled
        refresh()
    }

    fun setTimeout(timeout: LockTimeout) {
        AppLockSettings.timeout = timeout
        refresh()
    }

    fun disable() {
        AppLockSettings.clear()
        refresh()
    }

    // Hash mit vielen Runden, deshalb nicht auf dem Main-Thread
    fun checkPin(pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val correct = withContext(Dispatchers.Default) { AppLockSettings.checkPin(pin) }
            onResult(correct)
        }
    }

    fun setPin(pin: String, onSaved: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) { AppLockSettings.setPin(pin) }
            refresh()
            onSaved()
        }
    }
}
