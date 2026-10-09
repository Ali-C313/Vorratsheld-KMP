package com.example.vorratsheld.ui.components

import androidx.compose.runtime.Composable
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.expiry_expired_days
import com.example.vorratsheld.resources.expiry_in_days
import com.example.vorratsheld.resources.expiry_today
import com.example.vorratsheld.resources.expiry_tomorrow
import org.jetbrains.compose.resources.stringResource

// "Läuft morgen ab", "Seit 2 Tagen abgelaufen" usw.
@Composable
fun expiryLabel(daysLeft: Long): String = when {
    daysLeft < 0 -> stringResource(Res.string.expiry_expired_days, -daysLeft)
    daysLeft == 0L -> stringResource(Res.string.expiry_today)
    daysLeft == 1L -> stringResource(Res.string.expiry_tomorrow)
    else -> stringResource(Res.string.expiry_in_days, daysLeft)
}
