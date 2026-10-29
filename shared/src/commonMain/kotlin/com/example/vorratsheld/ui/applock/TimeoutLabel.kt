package com.example.vorratsheld.ui.applock

import androidx.compose.runtime.Composable
import com.example.vorratsheld.data.enums.LockTimeout
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.timeout_1min
import com.example.vorratsheld.resources.timeout_5min
import com.example.vorratsheld.resources.timeout_immediately
import com.example.vorratsheld.resources.timeout_start
import org.jetbrains.compose.resources.stringResource

// Anzeigetext für die Sperrzeit
@Composable
fun timeoutLabel(timeout: LockTimeout): String = stringResource(
    when (timeout) {
        LockTimeout.IMMEDIATELY -> Res.string.timeout_immediately
        LockTimeout.ONE_MINUTE -> Res.string.timeout_1min
        LockTimeout.FIVE_MINUTES -> Res.string.timeout_5min
        LockTimeout.ON_START_ONLY -> Res.string.timeout_start
    }
)
