package com.example.vorratsheld.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.action_delete
import org.jetbrains.compose.resources.stringResource

// Ziffernfeld 0–9 mit Löschen und optionaler Extra-Taste (z. B. Face ID)
@Composable
fun NumberPad(
    onDigit: (Char) -> Unit,
    onDelete: () -> Unit,
    enabled: Boolean,
    extraKey: (@Composable () -> Unit)?
) {
    val haptic = LocalHapticFeedback.current
    val rows = listOf("123", "456", "789")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                row.forEach { digit ->
                    DigitKey(
                        label = digit.toString(),
                        enabled = enabled,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onDigit(digit)
                        }
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
            Box(modifier = Modifier.size(76.dp), contentAlignment = Alignment.Center) {
                extraKey?.invoke()
            }
            DigitKey(
                label = "0",
                enabled = enabled,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onDigit('0')
                }
            )
            PadIconKey(
                icon = Icons.AutoMirrored.Filled.Backspace,
                description = stringResource(Res.string.action_delete),
                enabled = enabled,
                onClick = onDelete
            )
        }
    }
}
