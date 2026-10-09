package com.example.vorratsheld.util

import com.example.vorratsheld.data.enums.LockTimeout
import com.example.vorratsheld.data.settings.AppLockSettings
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Merkt sich, ob die App gerade gesperrt ist – beim Start gesperrt, wenn die Sperre an ist
object AppLock {
    private val _locked = MutableStateFlow(AppLockSettings.enabled)
    val locked: StateFlow<Boolean> = _locked.asStateFlow()

    private var backgroundSince: TimeMark? = null
    private var skipNextLock = false

    fun onBackground() {
        backgroundSince = TimeSource.Monotonic.markNow()
    }

    fun onForeground() {
        val since = backgroundSince ?: return
        backgroundSince = null
        if (skipNextLock) {
            skipNextLock = false
            return
        }
        if (!AppLockSettings.enabled) return
        val timeout = AppLockSettings.timeout
        if (timeout != LockTimeout.ON_START_ONLY &&
            since.elapsedNow() >= timeout.seconds.seconds
        ) {
            _locked.value = true
        }
    }

    // Für eigene Ausflüge aus der App, z. B. Fotoauswahl im Profil
    fun skipLockOnce() {
        skipNextLock = true
    }

    fun unlock() {
        _locked.value = false
    }
}
